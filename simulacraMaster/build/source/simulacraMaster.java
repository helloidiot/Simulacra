import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import peasy.*; 
import controlP5.*; 
import nervoussystem.obj.*; 
import java.util.*; 
import toxi.sim.grayscott.*; 
import toxi.math.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class simulacraMaster extends PApplet {

/////////////////////////////////////////////////////////////////////////////////////////////////
//
//                              simulacra
//                           september 2018
//
//                      by Joseph Rodrigues Marsh
//
//                 MA Computational Art Final Project
//
// simulacra is a generative animation software, which has been developed to allow the artist
// to create grotesque simulacrum with computational noise, where once rigid non-human forms
// grow swollen and deflate in seamless and satisfyingly endless loops
//
//                            Instructions
//
//  1. First select a shape to create in setup() by setting the corresponding boolean to 'true';
//  2. Select an update function in the Vert class if desired.
//    (some work better with some shapes than others)
//  3. Play with the GUI controls in order to create interesting behaviours
//
// n.b.
// The program must be restarted in order for a new shape and update to be selected.
// Unfortunately the GUI sizing controls are temperamental and can cause null pointer errors
// so it is recommended these are avoided while the program is running.
//
// User Input
// greyScott: keys '1'->'9', UP & DOWN effect coefficients, 'R' resets the simulation.
// Camera: 'A','S' & 'D' saves camera position, 'F','G' & 'H' loads position.
//
// Shape and animation parameters can be saved and loaded to a JSON file for retrieval. Files
// should be manually renamed according to the chosen shape and update modulator. For loading,
// the JSON should be renamed to 'controlP5.json' and placed in the project folder.
//
// With many thanks to Lior Ben-Gai, Goldsmiths for creating the boilerplate for PfA II from
// which this software was developed.
//
//                            Requirements
// PeasyCam, ControlP5, NervousSystem OBJExporter & ToxicLibs.
//
////////////////////////////////////////////////////////////////////////////////////////////////

// Import the required libraries







// Define required classes
OpenSimplexNoise simplexNoise;
GUI gui;

// Define the Grey Scott algorithm
// Uses toxiclibs greyScott Reaction Diffusion
// http://toxiclibs.org/2010/02/simutils-grayscott/
GrayScott gs;
boolean bReaction;
int gsWidth = 128;
int gsHeight = 128;
int feedW, feedH = 3;
int gsIterations = 20;
float gsRes = 2;
boolean bCreatures;
int numCreatures = 200;
PVector[] creatures = new PVector[numCreatures];

// Define camera and GUI
PeasyCam cam;

// Used to turn down zoom sensitivity on a track pad, make false if using a mouse
boolean bTrackpad = true;

// Initialise states for saving camera angles
CameraState state1, state2, state3;

// Initialise the GUI
ControlP5 cp5;

// Create the time iterator
int meshIt = 0;

// Define the forms as Artefacts, but instantiate as a shape
Artifact shape1, shape2;

// Create a colour palette
int lightBlue = color(154,227,221);
int palePink = color(232,194,202);
int orange = color(255,163,99);
int paleGrey = color(183,191,204);

// Assign viewport settings
int viewport_w = 1280;
int viewport_h = 720;

public void settings(){
  size(viewport_w, viewport_h, P3D);
  smooth(0);
}

public void setup() {

  // Cap frameRate so animations behave as expected
  // Animations are intended to be rendered at 23.98fps / 24fps.
  // Can cause runtime exception when using a class that requires some time to load (model, modelSequence)
  frameRate(24);

  // Initialise noise and GUI
  simplexNoise = new OpenSimplexNoise();
  gui = new GUI();

  // Initialise the Reaction Diffusion algorithm with initial values.
  gs = new GrayScott(gsWidth, gsHeight, true);
  gs.setCoefficients(0.028f, 0.084f, 0.095f, 0.03f);
  bReaction = false;
  bCreatures = false;

  // Setup PeasyCam
  cam = new PeasyCam(this, 200);
  cam.setMinimumDistance(0.0001f);
  cam.setMaximumDistance(10000);
  if (bTrackpad) cam.setWheelScale(.05f);
  state1 = cam.getState();

  //////////////////////////////////////////////////
  // This is where you select the shape to create //
  //////////////////////////////////////////////////

  plane =       false;
  hyperbolic =  true;
  sphere =      false;
  cube =        false;
  flower =      false;
  tongue =      false;
  barnsley =    false;
  model =       false;
  modelSeq =    false;
  image =       false;
  imgSeq =      false;
  grey =        false;

  // Instantiate the chosen shape
  if (plane)      shape1 = new Plane();
  if (hyperbolic) shape1 = new Hyperbolic();
  if (sphere)     shape1 = new Sphere();
  if (cube)       shape1 = new Cube();
  if (flower)     shape1 = new Flower();
  if (barnsley)   shape1 = new Barnsley();
  if (model)      shape1 = new Model();
  if (modelSeq)   shape1 = new ModelSequence();
  if (image)      shape1 = new ImageReader();
  if (imgSeq)     shape1 = new ImageSequence();

  if (tongue){
    animated = true;
    shape1 = new Tongue();
    }
  if (grey){
    bReaction = true;
    bCreatures = false;
    shape1 = new GreyScottPlane();
    }

  // Setup ControlP5
  gui.init(this);
}

public void draw() {

  // Set styling options
  background(125);

  if (toggle_grid)       drawGrid();
  if (toggle_axis)       drawAxis();
  if (toggle_Lights)     lights();
  if (toggle_Wireframe)  setStroke();
  if (!toggle_Wireframe) noStroke();

  // Setup, update and draw the shape
  shape1.setup();
  shape1.update(meshIt);
  shape1.draw(this.g, meshIt);

  // Draw the GUI
  gui.display(this);

  // Advance the iterator
  meshIt++;
}

////////////////////////
// GUIDES AND HELPERS
//////////////////////

public void drawAxis() {
  push();

  // X
  strokeWeight(2);
  stroke(255, 0, 0); // R
  line(0, 0, 0, 100, 0, 0);
  strokeWeight(4);
  point(100, 0, 0);
  // Y
  strokeWeight(2);
  stroke(0, 255, 0); // G
  line(0, 0, 0, 0, 100, 0);
  strokeWeight(4);
  point(0, 100, 0);
  // Z
  strokeWeight(2);
  stroke(0, 0, 255); // B
  line(0, 0, 0, 0, 0, 100);
  strokeWeight(4);
  point(0, 0, 100);

  pop();
}

public void drawGrid() {
  push();
  setStroke();
  float sz = 100;
  int nm = 8;
  // draw lines along the flat XY plane
  for(int i = 0; i <= nm; i++){
    line( (i * sz) - (sz*nm/2), -sz*nm/2, 0, (i * sz) - (sz*nm/2), sz*nm/2, 0);
    line( -sz*nm/2, (i * sz) - (sz*nm/2), 0, sz*nm/2, (i * sz) - (sz*nm/2), 0);
  }
  pop();
}

/////////////////////////
// EASING MAP FUNCTION //
/////////////////////////

// by Manohar Vanga
// https://sighack.com/post/easing-functions-in-processing
// https://github.com/sighack/easing-functions

// Easing types
final int QUADRATIC = 0;
final int SINUSOIDAL = 1;

// When the easing is applied
final int EASE_IN_OUT = 0;

// Easing mapping function
public float map2(float value, float start1, float stop1, float start2, float stop2, int type, int when) {

  float b = start2;
  float c = stop2 - start2;
  float t = value - start1;
  float d = stop1 - start1;
  float p = 0.5f;
  switch (type) {
  case QUADRATIC:
      if (when == EASE_IN_OUT) {
      t /= d/2;
      if (t < 1) return c/2*t*t + b;
      t--;
      return -c/2 * (t*(t-2) - 1) + b;
    }
    break;
  case SINUSOIDAL:
    if (when == EASE_IN_OUT) {
      return -c * cos(t/d * (PI/2)) + c + b;
    }
    break;
  };
  return 0;
}

////////////////////////
// GUI EVENT HANDLERS //
////////////////////////

// empty the shape and recreate its vertices
public void recreate(int theValue) {
  shape1.setup();
}

public void exportMesh(int theValue) {
  // Exports a single mesh of the current frame
  shape1.exportMesh("static", meshIt);
  notification = "SHAPE EXPORTED.";
}

public void exportMeshSequence(int theValue) {
  // Reset the Grey scott algorithm if desired, this can be commented out if the algorithm is being used as a texture
  gs.reset();

  // Calls the export sequence function
  shape1.exportMeshSequence("seq", meshIt);

  // Update the notification label wiht number of frames epxorted
  notification = str(numFrames_Slider) + " FRAMES EXPORTED.";
}

public void saveSettings(int theValue) {
    // Saves all controlP5 settings to a json file which can be renamed with corresponding shape for easy recall
    notification = "SETTINGS SAVED.";
    cp5.saveProperties();
}

public void loadSettings(int theValue) {
    // Loads all controlP5 settings, json file must be renamed to 'controlP5.json' before loading
    notification = "SETTINGS LOADED.";
    cp5.loadProperties();
}

public void resetRot(int theValue) {
    rotationX = 0.0f;
    rotationY = 0.0f;
    rotationZ = 0.0f;

    shape1.rotation.x = 0.0f;
    shape1.rotation.y = 0.0f;
    shape1.rotation.z = 0.0f;

    notification = "ROTATION RESET.";
}

public void resetScale(int theValue) {
    scaleMod = 1.0f;

    shape1.scale.x = 1.0f;
    shape1.scale.y = 1.0f;
    shape1.scale.z = 1.0f;

    notification = "SCALE RESET.";
}

public void animated(int theValue){
    animated = !animated;

    if (animated) notification = "ANIMATION MODE ACTIVE.";
    else if (!animated) notification = "ANIMATION MODE DISABLED.";
}

// GUI Event handlers which get called whenever a GUI value is changed
public void controlEvent(ControlEvent theEvent) {

  // When enabled, call the shape's setup whenever a parameter is changed
  if(auto_update){
    shape1.setup();

    // If using more than one shape, setup the second etc...
    // shape2.setup();
  }

  // Reset extrusion
  if(theEvent.getController().getName()=="resetExtrude") {
    cp5.getController("x_Slider").setValue(0.0f);
    cp5.getController("xMin").setValue(0.0f);
    cp5.getController("xMax").setValue(0.0f);
    cp5.getController("y_Slider").setValue(0.0f);
    cp5.getController("yMin").setValue(0.0f);
    cp5.getController("yMax").setValue(0.0f);
    cp5.getController("z_Slider").setValue(0.0f);
    cp5.getController("zMin").setValue(0.0f);
    cp5.getController("zMax").setValue(0.0f);
    notification = "EXTRUSION RESET.";
  }

  // Noise switches change the type of animated noise to apply

  if(theEvent.getController().getName()=="animN") {
    if (sineN)  cp5.getController("sineN").setValue(0);
    if (sine)   cp5.getController("sine").setValue(0);
    notification = "ANIMATED NOISE SELECTED.";
  }
  if(theEvent.getController().getName()=="sineN") {
    if (animN)  cp5.getController("animN").setValue(0);
    if (sine)   cp5.getController("sine").setValue(0);
    notification = "SINE NOISE SELECTED.";
  }
  if(theEvent.getController().getName()=="sine") {
    if (animN)  cp5.getController("animN").setValue(0);
    if (sineN)  cp5.getController("sineN").setValue(0);
    notification = "SINE WAVE SELECTED.";
  }
}

////////////////
// USER INPUT //
////////////////

public void keyPressed(){

  // Camera save / load controls
  switch(key) {
  case 'a':
    state1 = cam.getState();
    notification = "CAMERA 1 SAVED.";
    break;
  case 's':
    state2 = cam.getState();
    notification = "CAMERA 2 SAVED.";
    break;
  case 'd':
    state3 = cam.getState();
    notification = "CAMERA 3 SAVED.";
    break;
  case 'f':
    cam.setState(state1, 1000);
    notification = "CAMERA 1 LOADED.";
    break;
  case 'g':
    cam.setState(state2, 1000);
    notification = "CAMERA 2 LOADED.";
    break;
  case 'h':
    cam.setState(state3, 1000);
    notification = "CAMERA 3 LOADED.";
    break;
  }
}

public void keyReleased(){

  // Grey scott value modifications
  if (key>='1' && key<='9') {
    gs.setF(0.02f+(key-'1')*0.001f);
    println("F: " + gs.getF());
    notification = "GS K: " + str(gs.getF());
  }
  if (keyCode == UP) {
    gs.setK(gs.getK()+0.001f);
    println("K: " + gs.getK());
    notification = "GS K: " + str(gs.getK());
  }
  if (keyCode == DOWN) {
    gs.setK(gs.getK()-0.001f);
    println("K: " + gs.getK());
    notification = "GS K: " + str(gs.getK());
  }
  if (key == 'r'){
    gs.reset();
    notification = "GREY SCOTT RESET.";
    }
}

//////////////////////
// HELPER FUNCTIONS //
//////////////////////

public void push(){
  pushStyle();
  pushMatrix();
}

public void pop(){
  popStyle();
  popMatrix();
}

public void setStroke(){
  strokeWeight(stroke_Weight);
  stroke(stroke_Colour);
}
///////////////////////////
// Artifact Master Class //
///////////////////////////

// This is the master class which all other geometries inherit from.

public class Artifact{

  ////////////////////////
  // PROPERTIES
  //////////////////////

  ArrayList<Vert> vertices;
  PVector position;
  PVector rotation;
  PVector scale;

  float noiseScale = scale_Slider;
  float noiseRadius = radius_Slider;
  int numFrames = numFrames_Slider;
  int meshIterator = 0;

  ////////////////////////
  // CONSTRUCTOR
  //////////////////////
  Artifact(){

    // Set initial transformation values for each Artifact
    position = new PVector(0,0,0);
    rotation = new PVector(0,0,0);
    scale = new PVector(1.f,1.f,1.f);
    vertices = new ArrayList<Vert>();
    setup();
  }

  public void setup(){

    // This setup() function gets overridden by each classes own setup() function

    // Create an empty array for the vertices to populate
    vertices = new ArrayList<Vert>();
  }

  public void update(int it) {

    // Update the rotation by adding the GUI rotation amount to its current rotation
    rotation.y += rotationY;
    rotation.x += rotationX;
    rotation.z += rotationZ;

    // Update the scale by adding the GUI scale amount to its current scale
    scale.x = scaleMod;
    scale.y = scaleMod;
    scale.z = scaleMod;

    // If in animation mode...
    if (animated){

      // The XYZ min/max GUI sliders control the range of animated extrusion, as do scaleMin/Max & radiusMin/Max

      // Animate Simplex noise values with more Simplex Noise
      if (animN){
        float t = 1.0f * it / numFrames_Slider;
        float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        scale_Slider = map2(ns, -1, 1, scaleMin, scaleMax, QUADRATIC, EASE_IN_OUT);
        radius_Slider = map2(ns, -1, 1, radiusMin, radiusMax, QUADRATIC, EASE_IN_OUT);
      }
      // Animate Simplex noise values based on a sinewave of a returned looping noise value.
      else if (sineN){
        float t = 1.0f * it / numFrames_Slider;
        float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        scale_Slider = map2(sin(ns), -1, 1, scaleMin, scaleMax, QUADRATIC, EASE_IN_OUT);
        radius_Slider = map2(sin(ns), -1, 1, radiusMin, radiusMax, QUADRATIC, EASE_IN_OUT);
      }
      // Animated Simplex Noise values based on a sine wave
      else if (sine){
        float t = 4.0f * it / numFrames_Slider;
        scale_Slider = map2(sin(t), -1, 1, scaleMin, scaleMax, QUADRATIC, EASE_IN_OUT);
        radius_Slider = map2(sin(t), -1, 1, radiusMin, radiusMax, QUADRATIC, EASE_IN_OUT);
      }
    }

    // Update each vert every frame
    for(int i = 0; i < vertices.size(); i++){
      vertices.get(i).update(it);
    }

    // Advance the iterator every frame.
    it++;
  }

  // Draw all vertices using Points to a PGraphics object
  public void draw(PGraphics pg, int it){

    // DRAW ALL VERTICES AS POINTS //
    pushTransform(pg);

    pg.beginShape(POINTS);
    for (int i = 0; i < vertices.size(); i++){
      PVector p = vertices.get(i).position;
      pg.vertex(p.x, p.y, p.z);
    }
    pg.endShape();
    popTransform(pg);


  }

// EXPORTING MESHES

  // Uses the OBJExport library:
  // https://n-e-r-v-o-u-s.com/tools/obj/

  public void exportMesh(String fileName, int i){
    // Outputs an .obj file to the project folder.
    MeshExport output = (MeshExport) createGraphics(10, 10, "nervoussystem.obj.OBJExport", fileName + ".obj");
    output.beginDraw();
    this.draw(output, i);
    output.endDraw();
    output.dispose();
    println(fileName + " Export complete.");
  }

  public void exportMeshSequence(String fileName, int i){

    // Outputs a sequence of .obj files for use in Blender.

    // For the amount of frames specified with the numFrames slider;
    for (int len = 0; len <= numFrames_Slider * numFramesMult; len++){
      // Export a single obj named by its frame number, with 4 decimal places appended to aid import in Blender)
      OBJExport output = (OBJExport) createGraphics(10, 10, "nervoussystem.obj.OBJExport", fileName + nf(len, 4) + ".obj");
      output.beginDraw();
      this.setup();
      this.update(i);
      this.draw(output, i);
      output.endDraw();
      output.dispose();
      println("Exported " + " " + fileName + nf(len, 4));

      // Once a frame has been exported, advance the iterator and restart the loop.
      i++;
    }

}

  //////////////////////
  // HELPER FUNCTIONS //
  //////////////////////

  public void pushTransform(PGraphics pg){
    // perform all transformations
    pg.pushMatrix();
    pg.translate(position.x, position.y, position.z);
    pg.rotateX(rotation.x);
    pg.rotateY(rotation.y);
    pg.rotateZ(rotation.z);
    pg.scale(scale.x, scale.y, scale.z);
  }

  // pop out of all transformations
  public void popTransform(PGraphics pg){
      pg.popMatrix();
  }

  // Push vertices to arraylist
  // Version 1 - Single Vector
  public void pushVert(PVector v){
    vertices.add(new Vert(v.x,v.y,v.z));
  }
  // Version 2 - 3 floating point values
  public void pushVert(float x, float y, float z){
    vertices.add(new Vert(x,y,z));
  }

  // Version 1b - Removing a single Vector
  public void popVert(float x, float y, float z){
    vertices.remove(new Vert(x,y,z));
  }

  // Version 2b - Removing 3 floating point values
  public void popVert(PVector v){
    vertices.remove(new Vert(v.x,v.y,v.z));
  }


} // End of the Artifact class
///////////////////
// BARNSLEY FERN //
///////////////////

// A class for creating a simple Barnsley fern based on the classic Barnsley fern algorithm.

// References
// https://rosettacode.org/wiki/Barnsley_fern

// Boolean for inititialising the class
boolean barnsley;

public class Barnsley extends Artifact{

  // CONSTRUCTOR
  Barnsley(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  public void setup(){
    vertices = new ArrayList<Vert>();
    PVector tempVec = new PVector(0,0,0);

    PVector temp = new PVector(0,0,0);
    PVector prev = new PVector(0,0,0);
    float prevx = 0;
    float prevy = 0;

    float rnd;
    int i;
    int j;
    int iterations = 20000;
    int bkgrnd = 255;

    int n = 0;

    while (n++ < iterations) {

      rnd = random(100);

      // Stem
      if (rnd <= 1) { // 1%
        temp.x = 0;
        temp.y = 0.16f * prevy;
      }
      // Pinna
      else if (rnd <= 7) { // 7%
          temp.x = 0.2f * prev.x - 0.26f * prev.y;
          temp.y = 0.23f * prev.x + 0.22f * prev.y + 1.6f;
      }
      // Successively smaller leaflets
      else if (rnd <= 15) { // 85%
          temp.x = -0.15f * prev.x + 0.28f * prev.y;
          temp.y = 0.26f * prev.x + 0.24f * prev.y + 0.44f;
      }
      // Pinna
      else { // the other 7%
          temp.x = 0.85f * prev.x + 0.04f * prev.y;
          temp.y = -0.04f * prev.x + 0.85f * prev.y + 1.6f;
      }

      tempVec.x = 300 + temp.x * 108;
      tempVec.y = 800 - (temp.y * 108) * 0.7f;
      pushVert(tempVec);
      prev.x = temp.x;
      prev.y = temp.y;
    }
  }

  // DRAW
  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // Draw all vertices in the array as points
    pg.beginShape(POINTS);
    for(int i = 0; i < vertices.size(); i++){
      PVector p = vertices.get(i).position;
      pg.vertex(p.x, p.y, p.z);

    }
    pg.endShape();

    popTransform(pg);
    it++;
  }
} // End of Barnsley class
//   +----+
//  /    /|   //////////
// +----+ |   // CUBE //
// |    | +   //////////
// |    |/
// +----+

// A class which creates a hollow cube in 3D space.

// PROPERTIES
// Boolean for inititialising the class
boolean cube;
int cubeWidth, cubeDepth, cubeHeight;
int cubeSpacing;
int w, h, d, spacing;

public class Cube extends Artifact{

  // CONSTRUCTOR
  Cube(){
    super();
  }

  // SETUP
  public void setup(){
    vertices = new ArrayList<Vert>();

    // Assign a width and depth
    w = cubeWidth;
    h = cubeHeight;

    // Depth is double the size of the width and height as when rendering the camera will be placed inside the cube.
    d = cubeDepth;

    spacing = cubeSpacing;

    for(int k = 0; k < d; k++){
          for(int i = 0; i < w; i++){
            for(int j = 0; j < h; j++){

              // Determine whether a vertex is at the beginning or the end of its particular array
              boolean edge = (k == 0 || k == d-1) || (i == 0 || i == w-1) || (j == 0 || j == h-1);

              // Only push the vertex into the array if it is at the edge of the cube.
              if(edge){
                pushVert(i*spacing - (w*spacing/2.0f), j*spacing - (h*spacing/2.0f), k*spacing - (w*spacing/2.0f));
              }
            }
          }
        }
  }

  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // Draw all vertices in the array as points
    pg.beginShape(POINTS);

    for(int j = 0; j < vertices.size(); j++){
      PVector p = vertices.get(j).position;
      pg.vertex(p.x, p.y, p.z);
    }

    pg.endShape();
    popTransform(pg);

  }
} // End of Cube class
////////////
// FLOWER //
////////////

// REFERENCE
//
// Giant Pyrosomes - https://imgur.com/kn2LrvQ
// https://www.benfrederickson.com/flowers-from-simplex-noise/
//
// This class is used to make long undulating tubes and short compressed flowers.

// PROPERTIES
float flowerFrequency, flowerMagnitude, flowerIndependence, flowerSpacing, flowerZ, flowerAmount;
int flowerCount, flowerRadius, flowerSamples;
float flowerFreqMin, flowerFreqMax, flowerMagMin, flowerMagMax, flowerIndMin, flowerIndMax;

// Boolean for inititialising the class
boolean flower;

public class Flower extends Artifact{

  // CONSTRUCTOR
  Flower(){
    super();
  }

  // SETUP
  public void setup(){
    vertices = new ArrayList<Vert>();
    PVector tempVec = new PVector(0, 0, 0);

    // Draw the flower
    drawFlower( tempVec,  flowerRadius,  flowerFrequency,  flowerMagnitude,  flowerIndependence,  flowerSpacing,  flowerCount, flowerSamples);

  }

  // DRAW
  public void draw(PGraphics pg, int it){
    // If in animation mode, animate the frequency, magnitude and independence of the flower using looping simplex noise.
    if (animated){
      float t = 1.0f * it / numFrames_Slider;
      float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

      flowerFrequency = map2(ns, -1, 1, flowerFreqMin, flowerFreqMax, QUADRATIC, EASE_IN_OUT);
      flowerMagnitude = map2(ns, -1, 1, flowerMagMin, flowerMagMax, QUADRATIC, EASE_IN_OUT);
      flowerIndependence = map2(ns, -1, 1, flowerIndMin, flowerIndMax, QUADRATIC, EASE_IN_OUT);

    }

    pushTransform(pg);
    pg.beginShape(TRIANGLE_STRIP);
      for (int j = 0; j < vertices.size()-flowerSamples; j++){
        // Grab the first vertex in the array
        PVector p1 = vertices.get(j).position;
        // And the corresponding vertex on the next circle
        PVector p2 = vertices.get((j + flowerSamples)).position;
        // Add them to the buffer
        pg.vertex(p1.x, p1.y, p1.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
    pg.endShape();
    popTransform(pg);
  }

  // Draws a deformed circle depending on GUI specified values
  public void drawCircle(PVector v, float radius, float frequency, float magnitude, float seed, float s ){

    for (int i = 0; i < s; i++){
        float angle = (2 * PI * i) / s;

        float x = radius * sin(angle);
        float y = radius * cos(angle);

        float deformation = (float)simplexNoise.eval(x * frequency, y * frequency, seed) + 1;
        float z = radius * (1 + magnitude * deformation);
        float rad = radius * (1 + magnitude * deformation);

        pushVert(v.x + rad * x, v.y + rad * y, v.z + rad * z);

    }
  }

  public void drawFlower(PVector v, float radius, float frequency, float magnitude, float independence, float spacing, float count, float s){
    // adjust the radius so will have roughly the same size irregardless of magnitude
    radius /= (magnitude + 1);

    for (int i = 0; i < count; i++) {
        // draw a circle, the final parameter controlling how similar it is to other circles in this image
        drawCircle(v, radius, frequency, magnitude, i * independence, s);

        // shrink the radius of the next circle
        radius *= (1 - spacing);
        v.z += flowerZ;
    }
  }
} // End of Flower class
///////////////
// GUI CLASS //
///////////////

// GUI vars
String saveLocation;
String notification = " ";
int stroke_Weight, stroke_Colour;
Textlabel fpsLbl, noiseLbl, toggleNoiseLbl, extrudeLbl, toggleExtLbl, styleLbl, transformLbl, shapeLbl, mouseXLbl, mouseYLbl, verticesLbl, notificationLbl;
boolean toggle_axis = false, toggle_grid = false, auto_update = true;

// GUI positioning variables
int buffer = 96;
int initPosY = 300;
int initPosX = 10;
int sliderBuffer = 20;
int sliderWidth = 96;
int sliderHeight = 16;
int buttonWidth = 64;
int buttonHeight = 16;
int toggleRes = 16;

// GUI Colors
int pink = color(216,113,136);
int lightGrey = color(170,170,170);
int darkGrey = color(44,48,55);

// Transform vars
float rotationX, rotationY, rotationZ;
float scaleMod;

// Noise vars
float mod1, mod2;
float x_Slider, y_Slider, z_Slider;
float xMin, xMax, yMin, yMax, zMin, zMax;
float radius_Slider, scale_Slider;
int numFrames_Slider, numFramesMult;

// Browian vars
int numIterations_Slider;
float amp_Slider, freq_Slider, persistence_Slider;
float scaleMin, scaleMax, radiusMin, radiusMax;

// Grey scott vars
float greyScottAmt;

// Toggles
boolean toggle_X = true, toggle_Y = true, toggle_Z = true;
boolean animated = false;
boolean animN, sineN, sine;
boolean toggle_Lights = true, toggle_Wireframe = true;
boolean brownian = false;

class GUI{

  public void init(PApplet p){

    // Initialise controlP5
    cp5 = new ControlP5(p);
    cp5.setAutoDraw(false);

    // Change the default font
    PFont f = createFont("heart font by syoko",12);
    ControlFont font = new ControlFont(f);

    // Set GUI styling
    cp5.setColorForeground(lightGrey);
    cp5.setColorBackground(darkGrey);
    cp5.setFont(font);
    cp5.setColorActive(pink);

    // Draw labels for fps and values
    int infoPosY = 10;
    int infoBuffer = 15;
    fpsLbl = new Textlabel(cp5,"FPS",initPosX,infoPosY,128,16);
    mouseXLbl = new Textlabel(cp5,"MOUSEX",initPosX,(infoPosY+=infoBuffer),128,16);
    mouseYLbl = new Textlabel(cp5,"MOUSEY",initPosX,(infoPosY+=infoBuffer),128,16);
    verticesLbl = new Textlabel(cp5,"VERTICES",initPosX,(infoPosY+=infoBuffer),128,16);

    // Notifications
    push();
    notificationLbl = cp5.addTextlabel("noticationLbl").setPosition(initPosX,(infoPosY+=infoBuffer)).setSize(128,16).setColorValue(orange);
    pop();

    // Helper toggles
    int helperPosY = 100;
    cp5.addToggle("auto_update").setLabel("U/D").setPosition(initPosX,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_grid").setLabel("GRID").setPosition(initPosX+30,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_axis").setLabel("AXIS").setPosition(initPosX+60,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_Lights").setLabel("LIGHT").setPosition(initPosX+90,helperPosY).setSize(toggleRes,toggleRes);
    cp5.addToggle("toggle_Wireframe").setLabel("WIRE").setPosition(initPosX+120,helperPosY).setSize(toggleRes,toggleRes);

    // Styling sliders
    int stylePosY = 140;
    styleLbl = new Textlabel(cp5,"STYLE",initPosX, stylePosY, 130, 16);
    cp5.addSlider("stroke_Weight").setLabel("weight").setPosition(initPosX,(stylePosY+=sliderHeight)).setSize(sliderWidth,8).setValue(1).setRange(0, 5);
    cp5.addSlider("stroke_Colour").setLabel("colour").setPosition(initPosX,(stylePosY+=sliderHeight)).setSize(sliderWidth,8).setValue(1).setRange(0, 255);

    // Function buttons
    cp5.addButton("recreate").setPosition(initPosX,650).setSize(buttonWidth,buttonHeight).setLabel("RECREATE");
    cp5.addButton("exportMesh").setPosition(initPosX,670).setSize(buttonWidth,buttonHeight).setLabel("EXPORTMESH");
    cp5.addButton("exportMeshSequence").setPosition(initPosX,690).setSize(buttonWidth,buttonHeight).setLabel("SEQUENCE");
    cp5.addButton("saveSettings").setPosition(1080,690).setSize(buttonWidth-20,buttonHeight).setLabel("SAVE");
    cp5.addButton("loadSettings").setPosition(1080+(50),690).setSize(buttonWidth-20,buttonHeight).setLabel("LOAD");

    // Transformation sliders
    int transformPosY = 185;
    transformLbl = new Textlabel(cp5,"TRANSFORM",7, transformPosY, 130, 16);
    cp5.addSlider("scaleMod").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0f,3.0f).setValue(1.0f).setLabel("scale");
    cp5.addSlider("rotationX").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0f,0.2f).setValue(0.0f).setLabel("rotX");
    cp5.addSlider("rotationY").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0f,0.2f).setValue(0.0f).setLabel("rotY");
    cp5.addSlider("rotationZ").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(sliderWidth,8).setRange(0.0f,0.2f).setValue(0.0f).setLabel("rotZ");
    cp5.addButton("resetRot").setPosition(initPosX,(transformPosY+=sliderHeight)).setSize(buttonWidth,buttonHeight).setLabel("RESET ROT");
    cp5.addButton("resetScale").setPosition(initPosX+buttonWidth+5,transformPosY).setSize(buttonWidth,buttonHeight).setLabel("RESET SCL");

    /////////////////////////////////////////
    // Conditional controls for each shape //
    /////////////////////////////////////////

    int shapeInitPosY = 300;
    shapeLbl = new Textlabel(cp5,"SHAPE VALUES",7,shapeInitPosY,130,16);

    // Plane
    if (plane){
      cp5.addSlider("planeWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("width");
      cp5.addSlider("planeHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("height");
      cp5.addSlider("planeRes").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(2).setRange(0.0f,10.0f).setLabel("resolution");
    }

    // Flower
    else if (flower){
      cp5.addSlider("flowerRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(10).setRange(0, 30).setLabel("radius");
      cp5.addSlider("flowerFrequency").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0,1.0f).setLabel("frequency");
      cp5.addSlider("flowerMagnitude").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("magnitude");
      cp5.addSlider("flowerIndependence").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("independence");
      cp5.addSlider("flowerSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.01f).setRange(0.00f,0.50f).setLabel("spacing");
      cp5.addSlider("flowerCount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(100).setRange(0,1000).setLabel("count");
      cp5.addSlider("flowerZ").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(-0.50f).setRange(20,-20).setLabel("posZ");
      cp5.addSlider("flowerSamples").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(50.0f).setRange(0,200).setLabel("samples");
      cp5.addSlider("flowerFreqMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("freqMin");
      cp5.addSlider("flowerFreqMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("freqMax");
      cp5.addSlider("flowerMagMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("magMin");
      cp5.addSlider("flowerMagMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("magMax");
      cp5.addSlider("flowerIndMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("indMin");
      cp5.addSlider("flowerIndMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("indMax");
    }

    // Sphere
    else if (sphere){
      cp5.addSlider("sphereRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0, 100).setLabel("radius");
      cp5.addSlider("sphereResW").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0,100).setLabel("resW");
      cp5.addSlider("sphereResH").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0.0f,100.0f).setLabel("resH");
      cp5.addSlider("sphereAmount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,100.0f).setLabel("resH");
    }

    // Cube
    else if (cube){
      cp5.addSlider("cubeWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(32).setRange(0, 100).setLabel("width");
      cp5.addSlider("cubeHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(32).setRange(0,100).setLabel("height");
      cp5.addSlider("cubeDepth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(64).setRange(0.0f,100).setLabel("depth");
      cp5.addSlider("cubeSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(5).setRange(0.0f,100).setLabel("spacing");
    }

    // Tongue
    else if (tongue){
      cp5.addSlider("tongueRadius").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(9.0f).setRange(0, 30).setLabel("radius");
      cp5.addSlider("tongueFrequency").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0,1).setLabel("frequency");
      cp5.addSlider("tongueMagnitude").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("magnitude");
      cp5.addSlider("tongueIndependence").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("independence");
      cp5.addSlider("tongueSpacing").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,0.1f).setLabel("spacing");
      cp5.addSlider("tongueCount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(11).setRange(0,100).setLabel("count");
      cp5.addSlider("tongueZ").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(-10.0f).setRange(0.0f,-100).setLabel("Z");
      cp5.addSlider("tongueSamples").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(39.0f).setRange(0,200).setLabel("samples");
      cp5.addSlider("tongueAmount").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(5.0f).setRange(0,30).setLabel("amount");
      cp5.addSlider("tongueFreqMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("freqMin");
      cp5.addSlider("tongueFreqMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("freqMax");
      cp5.addSlider("tongueMagMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("magMin");
      cp5.addSlider("tongueMagMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("magMax");
      cp5.addSlider("tongueIndMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,1.0f).setLabel("indMin");
      cp5.addSlider("tongueIndMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.1f).setRange(0.0f,1.0f).setLabel("indMax");
    }

    // Hyperbolic paraboloid
    else if (hyperbolic){
      cp5.addSlider("hyperbolicWidth").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("width");
      cp5.addSlider("hyperbolicHeight").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(128).setRange(0,256).setLabel("height");
      cp5.addSlider("hyperbolicRes").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(2).setRange(0.0f,10.0f).setLabel("resolution");
      cp5.addSlider("hyperbolicWave").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.5f).setRange(0.0f,2.0f).setLabel("wave");
      cp5.addSlider("waveMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,2.0f).setLabel("waveMin");
      cp5.addSlider("waveMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(0.0f,2.0f).setLabel("waveMax");
      cp5.addSlider("hyperbolicFx").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(3.0f).setRange(-3.0f,3.0f).setLabel("fx");
      cp5.addSlider("fxMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3f).setRange(0.0f,3.0f).setLabel("fxMin");
      cp5.addSlider("fxMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3f).setRange(0.0f,3.0f).setLabel("fxMax");
      cp5.addSlider("hyperbolicFy").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(3.0f).setRange(-3.0f,3.0f).setLabel("fy");
      cp5.addSlider("fyMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3f).setRange(0.0f,3.0f).setLabel("fyMin");
      cp5.addSlider("fyMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.3f).setRange(0.0f,3.0f).setLabel("fyMax");
      cp5.addSlider("hyperbolicAmplify").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(1.0f).setRange(-10.0f,10.0f).setLabel("amplify");
      cp5.addSlider("ampMin").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-10.0f,10.0f).setLabel("ampMin");
      cp5.addSlider("ampMax").setPosition(initPosX,(shapeInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-10.0f,10.0f).setLabel("ampMax");
    }

    // Extrusion values
    int extInitPosX = 1080;
    int extInitPosY = 20;
    extrudeLbl = new Textlabel(cp5,"EXTRUSION",extInitPosX,extInitPosY,130,16);
    cp5.addSlider("x_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("X");
    cp5.addSlider("xMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("xMin");
    cp5.addSlider("xMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("xMax");
    cp5.addSlider("y_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("Y");
    cp5.addSlider("yMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("yMin");
    cp5.addSlider("yMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("yMax");
    cp5.addSlider("z_Slider").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("Z");
    cp5.addSlider("zMin").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("zMin");
    cp5.addSlider("zMax").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setValue(0.0f).setRange(-100,100).setLabel("zMax");

    // Extrusion toggles
    toggleExtLbl = new Textlabel(cp5,"TOGGLE EXTRUSION",extInitPosX,(extInitPosY+=sliderBuffer),130,16);
    cp5.addToggle("toggle_X").setPosition(extInitPosX,(extInitPosY+=sliderBuffer)).setSize(toggleRes,toggleRes).setLabel("X");
    cp5.addToggle("toggle_Y").setPosition(extInitPosX+(toggleRes*2),extInitPosY).setSize(toggleRes,toggleRes).setLabel("Y");
    cp5.addToggle("toggle_Z").setPosition(extInitPosX+(toggleRes*4),extInitPosY).setSize(toggleRes,toggleRes).setLabel("Z");
    cp5.addToggle("resetExtrude").setPosition(extInitPosX+(toggleRes*6),extInitPosY).setSize(toggleRes,toggleRes).setLabel("RESET");

    // Noise values
    int noiseInitPosX = 1080;
    int noiseInitPosY = 280;
    noiseLbl = new Textlabel(cp5,"NOISE",noiseInitPosX,noiseInitPosY,130,16);
    cp5.addSlider("mod1").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.1f).setRange(-200,200).setLabel("mod1");
    cp5.addSlider("mod2").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(-200,200).setValue(4).setLabel("mod2");
    cp5.addSlider("radius_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.5f).setRange(0,2).setLabel("radius");
    cp5.addSlider("scale_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setValue(0.1f).setRange(0.0f,0.30f).setLabel("scale");
    cp5.addSlider("numFrames_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0,500).setValue(161).setLabel("numFrames");
    cp5.addSlider("numFramesMult").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(1,4).setValue(1).setLabel("frameMult");
    cp5.addSlider("scaleMin").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0f,0.30f).setValue(0.0f).setLabel("scaleMin");
    cp5.addSlider("scaleMax").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0f,0.30f).setValue(0.0f).setLabel("scaleMax");
    cp5.addSlider("radiusMin").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0f,2.0f).setValue(0.0f).setLabel("radiusMin");
    cp5.addSlider("radiusMax").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(0.0f,2.0f).setValue(0.0f).setLabel("radiusMax");
    cp5.addSlider("greyScottAmt").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(buffer,16).setRange(-300.0f,300.0f).setValue(0.0f).setLabel("greyAmt");

    // Brownian noise values
    cp5.addSlider("numIterations_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(1,12).setValue(4).setLabel("iterations");
    cp5.addSlider("amp_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(1.0f,5.0f).setValue(1.0f).setLabel("amp");
    cp5.addSlider("freq_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(0.0f,0.1f).setValue(0.00f).setLabel("frequency");
    cp5.addSlider("persistence_Slider").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,sliderHeight).setRange(0.0f,2.0f).setValue(.5f).setLabel("persistence");

    // Noise toggles
    toggleNoiseLbl = new Textlabel(cp5,"TOGGLE ANIMATION TYPE",noiseInitPosX,(noiseInitPosY+=sliderBuffer),130,16);
    cp5.addButton("animated").setPosition(noiseInitPosX,(noiseInitPosY+=sliderBuffer)).setSize(sliderWidth,toggleRes).setLabel("animated");
    cp5.addToggle("animN").setPosition(noiseInitPosX,(noiseInitPosY+=(sliderBuffer))).setSize(toggleRes,toggleRes).setLabel("NS");
    cp5.addToggle("sineN").setPosition(noiseInitPosX+(toggleRes*2),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("SN");
    cp5.addToggle("sine").setPosition(noiseInitPosX+(toggleRes*4),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("SIN");
    cp5.addToggle("bReaction").setPosition(noiseInitPosX+(toggleRes*6),noiseInitPosY).setSize(toggleRes,toggleRes).setLabel("GREY SCOTT");

    // END init()
  }

  public void display(PApplet p){

    // Disable camera control on GUI interaction
    if (cp5.isMouseOver()) {
      cam.setActive(false);
    } else {
      cam.setActive(true);
    }

    // Draw the GUI on top of camera view
    hint(DISABLE_DEPTH_TEST);
    cam.beginHUD();
    cp5.draw();
    drawLabels(p);
    cam.endHUD();
    hint(ENABLE_DEPTH_TEST);
  }

  public void drawLabels(PApplet p){

    // Information labels
    fpsLbl.setValueLabel("FPS:      " + floor(frameRate));
    mouseXLbl.setValueLabel("MOUSEX:   " + mouseX);
    mouseYLbl.setValueLabel("MOUSEY:   " + mouseY);
    verticesLbl.setValueLabel("VERTICES: " + shape1.vertices.size());
    fpsLbl.draw(p);
    mouseXLbl.draw(p);
    mouseYLbl.draw(p);
    verticesLbl.draw(p);
    styleLbl.draw(p);
    transformLbl.draw(p);

    // Notifications
    notificationLbl.setValueLabel(notification);
    notificationLbl.draw(p);

    // Title labels
    extrudeLbl.draw(p);
    toggleExtLbl.draw(p);
    toggleNoiseLbl.draw(p);
    noiseLbl.draw(p);
    shapeLbl.draw(p);
  }
}
//////////////////////
// GREY SCOTT PLANE //
//////////////////////

// Reference
// http://toxiclibs.org/2010/02/simutils-grayscott/

// PROPERTIES

// Boolean for inititialising the class
boolean grey;

// For choosing the feed type
boolean bGSImage = true;
PImage img;

public class GreyScottPlane extends Artifact{  // Plane is a type of Artifact

  // PROPERTIES
  int wid;
  int hei;
  float scl;

  int feedW = 4;
  int feedH = 4;

  int gsIterations = 10;

  // CONSTRUCTOR
  GreyScottPlane(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  // override setup function to create a plane
  public void setup(){
    // Initialise our empty ArrayList and initial Vector
    vertices = new ArrayList<Vert>();
    PVector tempVec = new PVector(0,0,0);

    // Load an image to be used for feed (if not using the creatures)
    // Needs to be the same size as the shape used i.e 128x128 pixels
    if (bGSImage) img = loadImage("simulacra.png");

    int posX = (int)map(mod1, -200, 200, 0, gsWidth);
    int posY = (int)map(mod2, -200, 200, 0, gsHeight);

    // If using the image seed mode, add the image to the toxiclibs grey scott algorithm
    if (bGSImage) gs.seedImage(img.pixels, img.width, img.height);

    for(int i = 0; i < gsWidth; i++){
      for(int j = 0; j < gsHeight; j++){

        tempVec.x = i*gsRes - (gsWidth*scl/2.0f);
        tempVec.y = j*gsRes - (gsHeight*scl/2.0f);

        float cellValue = gs.v[(i * gsWidth) + j];
        float mod = map(cellValue, 0, 10, 0, greyScottAmt);

        tempVec.z = mod;

        pushVert(tempVec);
      }
    }
  }

  // DRAW
  // override draw function to draw a plane
  public void draw(PGraphics pg, int it){

    // Update the algorithm depending on the number of iterations set by the GUI
    for(int i = 0; i < gsIterations; i++) gs.update(1);

    // If we're in creature mode
    if (bCreatures){

      // Loop through each creature
      for (int i = 0; i < creatures.length; i++){
        // Assign it an empty vector
        creatures[i] = new PVector(0,0,0);

        // Generate an X & Y position value based on a looping simplex noise algorithm
        creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004f, (it + (i*200)) * 0.005f);
        creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004f, (it + (i*300)) * 0.005f);
        creatures[i].z = 0;

        // Map the returned value to the width and height of the shape
        int x = (int)map(creatures[i].x, -1, 1, 0, gsWidth);
        int y = (int)map(creatures[i].y, -1, 1, 0, gsHeight);

        // And place feed the algorithm at each creatures location
        gs.setRect(x, y, feedW, feedH);

        // This gives the impression of creatures crawling over the shape, pressing down on the raised sections
      }
    }

    // Otherwise, just set a small amount of feed in the centre of the shape
    else if (!bCreatures){
        gs.setRect(gsWidth/2, gsHeight/2, 3, 3);
    }

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < gsWidth -1;i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < gsHeight - 1 ;j++){

        PVector p = vertices.get( (i * gsWidth) + j ).position;
        PVector p2 = vertices.get( ((i+1) * gsHeight) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

  }
} // End of Grey Scott Plane class
///////////////////////////
// HYPERBOLIC PARABOLOID //
///////////////////////////

// Reference
// https://en.wikipedia.org/wiki/Saddle_point
// https://en.wikipedia.org/wiki/Paraboloid
// http://mathworld.wolfram.com/HyperbolicParaboloid.html

// A class used to create bulging planes which can then be modulated to give the impression of soft body dynamics.

// PROPERTIES
int hyperbolicWidth, hyperbolicHeight;
float hyperbolicRes, hyperbolicWave, hyperbolicFx, hyperbolicFy, hyperbolicAmplify, ampMin, ampMax, waveMin, waveMax, fxMin, fxMax, fyMin, fyMax;
int wid;
int hei;
float scl;

boolean hyperbolic;

public class Hyperbolic extends Artifact{

  // CONSTRUCTOR
  Hyperbolic(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  public void setup(){
    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Create an empty vector
    PVector tempVec = new PVector(0,0,0);
    float wave = 2 * PI * sq(hyperbolicWave);

    // Assign GUI variables to local
    wid = hyperbolicWidth;
    hei = hyperbolicHeight;
    scl = hyperbolicRes;

    for(int i = 0; i < wid; i++){
      for(int j = 0; j < hei; j++){

        // Assign an XY position for the vector as with a simple Plane
        tempVec.x = i * scl - (wid * scl / 2);
        tempVec.y = j * scl - (hei * scl / 2);

        float fx = map(tempVec.x, 0, wid, 0, hyperbolicFx);
        float fy = map(tempVec.y, 0, hei, 0, hyperbolicFy);

        // Calculate the hyperbola and apply to the Z value of the vertex
        float gaussian = exp(-(sq(fx) + sq(fy)) / wave);
        tempVec.z = gaussian * (dist(i, j, fx, fy)) * hyperbolicAmplify; // amplify

        // If using Reaction Diffusion, generate a  value for the specific vertex, and append it to the array
        if (bReaction){
          // Grab the value from the .v toxiclibs array
          float cellValue = gs.v[(i * gsWidth) + j];
          // Map it to a workable value
          float mod = map(cellValue, 0, -10, 0, greyScottAmt);

          pushVert(tempVec.x, tempVec.y, tempVec.z + mod);
        }

        // Add the vertex to the array
        else {
          pushVert(tempVec.x, tempVec.y, tempVec.z);
          }
      }
    }
  }

  // DRAW
  // Override draw function to draw a hypbolic paraboloid
  public void draw(PGraphics pg, int it){

    // For the reaction diffusion
    if (bReaction) {
      // Update the algorithm depending on the number of iterations set by the GUI
      for(int i = 0; i < gsIterations; i++) gs.update(1);

      // If we're in creature mode
      if (bCreatures){

        // Loop through each creature
        for (int i = 0; i < creatures.length; i++){
          // Assign it an empty vector
          creatures[i] = new PVector(0,0,0);

          // Generate an X & Y position value based on a looping simplex noise algorithm
          creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004f, (it + (i*200)) * 0.005f);
          creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004f, (it + (i*300)) * 0.005f);
          creatures[i].z = 0;

          // Map the returned value to the width and height of the shape
          int x = (int)map(creatures[i].x, -1, 1, 0, hyperbolicWidth);
          int y = (int)map(creatures[i].y, -1, 1, 0, hyperbolicHeight);

          // And place feed the algorithm at each creatures location
          gs.setRect(x, y, feedW, feedH);

          // This gives the impressions of creatures crawling across the shape, pressing down on the raised sections
        }
      }

      // Otherwise, just set a small amount of feed in the centre of the shape
      else if (!bCreatures){
          gs.setRect(hyperbolicWidth/2, hyperbolicHeight/2, 3, 3);
      }
    }

    // If in animation mode, modulate the hyperbolic amplification, wave, Fx and Fy values according to the return noise value
    if (animated){
      float t = 1.0f * it / numFrames_Slider;
      float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

      hyperbolicAmplify = map2(ns, -1, 1, ampMin, ampMax, QUADRATIC, EASE_IN_OUT);
      hyperbolicWave = map2(ns, -1, 1, waveMin, waveMax, QUADRATIC, EASE_IN_OUT);
      hyperbolicFx = map2(ns, -1, 1, fxMin, fxMax, QUADRATIC, EASE_IN_OUT);
      hyperbolicFy = map2(ns, -1, 1, fyMin, fyMax, QUADRATIC, EASE_IN_OUT);
    }

    pushTransform(pg);

    // Draw every vertex as a triangle strip
    for(int i = 0; i < wid-1; i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < hei-1; j++){

        // Grab the first vertex in the column
        PVector p = vertices.get( (i * wid) + j ).position;

        // The grab the corresponding vertex in the next row
        PVector p2 = vertices.get( ((i + 1) * hei) + j).position;

        // And append them to the array
        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }

    popTransform(pg);
    it++;
  }
} // End of the Hyperbolic class
//////////////////
// IMAGE READER //
//////////////////

// This class loads a two-dimensional image, and uses its brightness values to extrude a plane in z-space.

// Boolean for inititialising the class
boolean image;

public class ImageReader extends Plane{

  // PROPERTIES
  PImage img;
  String imageName;

  ImageReader() {
    super();
  }

  // SETUP
  public void setup(){

    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Enter the name of the image to load
    imageName = "simulacra";

    // Load the chosen image
    img = loadImage(imageName + ".png");

    // Along with its pixels
    img.loadPixels();

    // Assign the width of the plane to be the same width and height of the loaded image
    wid = img.width;
    hei = img.height;
    scl = 2;

    // Loop through every vertex, assigning an X & Y value, while calling a custom function to assign a Z value
    for(int i = 0; i < wid; i++){
      for(int j = 0; j < hei; j++){
        pushVert(i*scl - (wid*scl/2.0f),
                 j*scl - (hei*scl/2.0f),
                 getZAt(i, j));
      }
    }

    // Update the pixels
    img.updatePixels();
  }

  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < wid -1;i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < hei - 1 ;j++){

        PVector p = vertices.get( (i * wid) + j ).position;
        PVector p2 = vertices.get( ((i+1) * hei) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

  }

  // Find the corresponding vertex and map its brightness value to a chosen range via the GUI
  public float getZAt(int i, int j){
    float res = map(brightness(img.pixels[ j * wid + i]), 255, 0, 0, z_Slider);
    return res;
  }

} // End of the Image Reader class
////////////////////
// IMAGE SEQUENCE //
////////////////////

// This class is similar to Image Sequence, but it loads a sequence of images, allowing for the import and modulation of videos in 3D space.
// Videos should be exported as Photo JPEG sequences and name sequentially according to their frame i.e. '[sequenceName]0.png', '[sequenceName]1.png' etc.
// Remember to also assign the amount of frames to 'frameAmount'.

// Reference
// https://processing.org/examples/sequential.html

// Boolean for inititialising the class
boolean imgSeq;

public class ImageSequence extends Plane{

  // PROPERTIES
  PImage img;
  String sequenceName;
  int frameCounter = 0;
  int frameAmount = 235;

  ImageSequence() {
    super();
  }

  // SETUP
  public void setup(){
    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Create an empty vector
    // PVector tempVec = new PVector(0,0,0);

    // Enter the name of the image to load
    sequenceName = "eyes";

    // Load the image by appending the current frame number
    img = loadImage(sequenceName + "/" + frameCounter + ".png");
    img.loadPixels();

    // Assign the width and height of the plane to be the same as the image
    wid = img.width;
    hei = img.height;
    scl = 2;

    // Loop through every vertex, assigning an X & Y value, while calling a custom function to assign a Z value
    for(int i = 0; i < wid; i++){
      for(int j = 0; j < hei; j++){

        pushVert(i*scl - (wid*scl/2.0f),
                 j*scl - (hei*scl/2.0f),
                 getZAt(i, j));
      }
    }

    img.updatePixels();

    // Advance to the next frame
    frameCounter++;

    // If the end of the sequence is reached, return to the first image in the sequence
    if (frameCounter >= frameAmount){
      frameCounter = 0;
    }
  }

  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < wid - 1; i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < hei - 1 ; j++){

        PVector p = vertices.get((i * wid) + j).position;
        PVector p2 = vertices.get(((i+1) * hei) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

    frameCounter++;
  }

  // Find the corresponding vertex and map its brightness value to a chosen range via the GUI
  public float getZAt(int i, int j){
    float res = map(brightness(img.pixels[ j * wid + i]), 255, 0, 0, z_Slider);
    return res;
  }

} // End of the Image Sequence class.
////////////
// MODEL  //
////////////

// A class which allows for the import and manipulation of a 3D model
// The model should be in .obj format and triangulated.

// Reference

// For recursively appending children of an obj;
// https://stackoverflow.com/questions/40937648/processing-obj-extract-vertex

// 'myFace' model generated via 'Large Pose 3D Face Reconstruction from a Single
// Image via Direct Volumetric CNN Regression' from an image of myself.
// http://cvl-demos.cs.nott.ac.uk/vrn/

// Boolean for inititialising the class
boolean model;

String modelName = "myFace";

public class Model extends Artifact{

  // PROPERTIES
  PShape importedModel;

  // CONSTRUCTOR
  Model(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  public void setup(){
    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Load the specified 3D model
    importedModel = loadShape(modelName + ".obj");
    // loadModel();

    // Get all vertices of model
    getVertices(importedModel, vertices);
  }

  // DRAW
  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // Loop through every vertex and draw the model as TRIANGLES
    pg.beginShape(TRIANGLES);

    // Or QUADS if the model hasn't been triangulated
    // pg.beginShape(QUADS);

    for (int i = 0; i < vertices.size(); i++){
      PVector p = vertices.get(i).position;
      pg.vertex(p.x, p.y, p.z);
    }
    pg.endShape();
    popTransform(pg);

  }

  public void getVertices(PShape shape, ArrayList<Vert> verts){

    // For each face in the current mesh
    for (int i = 0; i < shape.getChildCount(); i++){

      PShape child = shape.getChild(i);
      int numChildren = child.getChildCount();

      // if there are nested elements, recurse through the children / nest
      if (numChildren > 0){
        for (int j = 0; j < numChildren; j++){
          getVertices(child.getChild(j), verts);
        }
      }
      else{
        // If we have reached the last child, append the its vertices to the Array
        for (int k = 0; k < child.getVertexCount(); k++){
          pushVert(child.getVertex(k));
        }
      }
    }
  }
} // End of Model class
////////////////////
// MODEL SEQUENCE //
////////////////////

// A class which allows for importing a sequence of 3D models which can be manipulated as a sequence.
// Sequences should be exported as .OBJ sequences and named sequentially according to their frame
// i.e. '[sequenceName]0.png', '[sequenceName]1.png' etc.
// It is designed for looping animations which happen 'in place' e.g. walk cycles.
// Remember to set the total number of frames with 'frameAmount' so the animation loops correctly.

// Boolean for inititialising the class
boolean modelSeq;

String sequenceName = "simpleWalk";
int frameCounter = 1;
int frameAmount = 51;

public class ModelSequence extends Artifact{  // ModelSequence is a type of Artifact

  // PROPERTIES
  PShape importedModel;

  // CONSTRUCTOR
  ModelSequence(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  public void setup(){

    // Initialise the empty array
    vertices = new ArrayList<Vert>();

    // Load the specified 3D model by appending the current frame number
    importedModel = loadShape(sequenceName + "/" + frameCounter + ".obj");

    // Calls a function to return all vertices of the model
    getVertices(importedModel, vertices);
  }

  // DRAW
  public void draw(PGraphics pg, int it){

    // Draw each vertex in the array as TRIANGLES, as long as the models are triangulated
    pushTransform(pg);
    pg.beginShape(TRIANGLES);
    for (int i = 0; i < vertices.size(); i++){
      PVector p = vertices.get(i).position;
      pg.vertex(p.x, p.y, p.z);
    }
    pg.endShape();
    popTransform(pg);

    // Advance the frame number
    frameCounter++;

    // If the frameCount has reached the final frame, return to the first frame
    if (frameCounter >= frameAmount){
      frameCounter = 1;
    }
  }

  public void getVertices(PShape shape, ArrayList<Vert> verts){

    // For each face in the current mesh
    for (int i = 0; i < shape.getChildCount(); i++){

      PShape child = shape.getChild(i);
      int numChildren = child.getChildCount();

      // if there are nested elements, recurse through the children / nest
      if (numChildren > 0){
        for (int j = 0; j < numChildren; j++){
          getVertices(child.getChild(j), verts);
        }
      }
      else{
        // If we have reached the last child, append the its vertices to the Array
        for (int k = 0; k < child.getVertexCount(); k++){
          pushVert(child.getVertex(k));
        }
      }
    }
  }
} // End of the Model Sequence class
//////////////////////
// PLANE PROPERTIES //
//////////////////////

int planeWidth, planeHeight;
float planeRes;
boolean plane;

public class Plane extends Artifact{  // Plane is a type of Artifact

  // CONSTRUCTOR
  Plane(){
    // Calls the contructor of the super class
    super();
  }

  // SETUP
  // override setup function to create a plane
  public void setup(){

    vertices = new ArrayList<Vert>();
    PVector tempVec = new PVector(0,0,0);

    for(int i = 0; i < planeWidth; i++){
      for(int j = 0; j < planeHeight; j++){

        tempVec.x = i*planeRes - (planeWidth*scl/2.0f);
        tempVec.y = j*planeRes - (planeHeight*scl/2.0f);
        tempVec.z = 0;
        pushVert(tempVec);
      }
    }
  }

  // DRAW
  // override draw function to draw a plane
  public void draw(PGraphics pg, int it){

    pushTransform(pg);

    // draw vertices on plane with triangles
    for(int i = 0; i < planeWidth -1;i++){
      pg.beginShape(TRIANGLE_STRIP);
      for(int j = 0; j < planeHeight - 1 ;j++){

        PVector p = vertices.get( (i * planeWidth) + j ).position;
        PVector p2 = vertices.get( ((i+1) * planeHeight) + j).position;

        pg.vertex(p.x, p.y, p.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();
    }
    popTransform(pg);

  }
} // End of Plane class
////////////
// SPHERE //
////////////

// This class creates a simple sphere 3D space, meshing the points using a TRIANGLE_STRIP.

// Reference
// The Coding Train // Spherical Geometry // https://www.youtube.com/watch?v=RkuBWEkBrZA

int sphereRadius, sphereResW, sphereResH, sphereAmount;
boolean sphere;

// int feedW = 3;
// int feedH = 3;
// int gsIterations = 20;

public class Sphere extends Artifact{

  // CONSTRUCTOR
  Sphere(){
    super();
  }

  // SETUP
  // override setup function to create a plane
  public void setup(){
    // Initialise the array
    vertices = new ArrayList<Vert>();

    // Create an empty vector
    PVector tempVec = new PVector(0, 0, 0);

    for (int i = 0; i < sphereResW+1; i++){
      // Create a mapped longitude value dpeending on the iterator position
      float lon = map(i, 0, sphereResW, 0.0001f, PI-0.0001f);

      for (int j = 0; j < sphereResH+1; j++){
        // Create a laitude value according to the iterator
        float lat = map(j, 0, sphereResH, 0.0001f, TWO_PI-0.0001f);

        // Assign X, Y & Z values according to spherical formula
        tempVec.x = (sphereRadius-1) * sin(lon) * cos(lat);
        tempVec.y = (sphereRadius-1) * sin(lon) * sin(lat);
        tempVec.z = (sphereRadius-1) * cos(lon);

        // If using reaction diffusion, generate a grey scott value for each vertex and push to the array
        if (bReaction){
          float cellValue = gs.v[(i * gsWidth) + j];
          float mod = map(cellValue, 0, -10, 0, greyScottAmt);
          pushVert(tempVec.x, tempVec.y, tempVec.z + mod);
          }

        // Otherwise push the created values to the array
        else {
          pushVert(tempVec.x, tempVec.y, tempVec.z);
        }
      }
    }
  }

  // DRAW
  public void draw(PGraphics pg, int it){

    // If using reaction diffusion...
    if (bReaction) {
      // Update the algorithm depending on the number of iterations set by the GUI
      for(int i = 0; i < gsIterations; i++) gs.update(1);

      // If we're in creature mode
      if (bCreatures){

        // Loop through each creature
        for (int i = 0; i < creatures.length; i++){
          // Assign it an empty vector
          creatures[i] = new PVector(0,0,0);

          // Generate an X & Y position value based on a looping simplex noise algorithm
          creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004f, (it + (i*200)) * 0.005f);
          creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004f, (it + (i*300)) * 0.005f);
          creatures[i].z = 0;

          // Map the returned value to the width and height of the shape
          int x = (int)map(creatures[i].x, -1, 1, 0, gsWidth);
          int y = (int)map(creatures[i].y, -1, 1, 0, gsHeight);

          // And place feed the algorithm at each creatures location
          gs.setRect(x, y, feedW, feedH);

          // This gives the impressions of creatures crawling across the shape, pressing down on the raised sections
        }
      }

      // Otherwise, just set a small amount of feed in the centre of the shape
      else if (!bCreatures){
          gs.setRect(gsWidth/2, gsHeight/2, 3, 3);
      }
    }

    for (int i = 0; i < sphereResH; i++){

      // Loop through the height of the sphere
      pg.beginShape(TRIANGLE_STRIP);
      for (int j = 0; j < sphereResW+1; j++){

        // Grab the initial vector and draw it
        PVector p1 = vertices.get(i*sphereRadius+j).position;
        pg.vertex(p1.x, p1.y, p1.z);
        // Grab the corresponding vector on the next strip and draw it
        PVector p2 = vertices.get((i+1)*sphereRadius+j).position;
        pg.vertex(p2.x, p2.y, p2.z);
      }

      // End the shape
      pg.endShape();
    }

    // Force the radius so the TRIANGLE_STRIPS align
    sphereResH = sphereResW;
    sphereRadius = sphereResW + 1;
  }
} // End of Sphere class
////////////
// TONGUE //
////////////

// This class is an development of the flower class.
// The intention is to create forms which waggle and wave like a tongue.

float tongueFrequency, tongueMagnitude, tongueIndependence, tongueSpacing, tongueZ, tongueAmount;
int tongueCount, tongueRadius, tongueSamples;
float tongueFreqMin, tongueFreqMax, tongueMagMin, tongueMagMax, tongueIndMin, tongueIndMax;
boolean tongue;

public class Tongue extends Artifact{

  // PROPERTIES
  // float samples;
  PVector tempVec;
  int timer = 0;
  // CONSTRUCTOR
  Tongue(){
    super();
  }

  // SETUP
  public void setup(){
    vertices = new ArrayList<Vert>();

    // If not in animation mode, draw a simple tongue
    if (!animated){
      tempVec = new PVector(0, 0, 0);
      drawTongue( tempVec,  tongueRadius,  tongueFrequency,  tongueMagnitude,  tongueIndependence,  tongueSpacing,  tongueCount, tongueSamples, timer);
    }

  }

  // DRAW
  // override draw function to draw a plane
  public void draw(PGraphics pg, int it){
    if (animated){
      // Create a new empty vector
      tempVec = new PVector(0, 0, 0);

      // Generate a looping noise value for shape animation
      float t = 1.0f * it / numFrames_Slider;
      float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

      // MOdulate the frequency, magnitude and independence of the shape with GUI specified values
      tongueFrequency = map2(ns, -1, 1, tongueFreqMin, tongueFreqMax, QUADRATIC, EASE_IN_OUT);
      tongueMagnitude = map2(ns, -1, 1, tongueMagMin, tongueMagMax, QUADRATIC, EASE_IN_OUT);
      tongueIndependence = map2(ns, -1, 1, tongueIndMin, tongueIndMax, QUADRATIC, EASE_IN_OUT);

      // Draw the tongue
      drawTongue( tempVec,  tongueRadius,  tongueFrequency,  tongueMagnitude,  tongueIndependence,  tongueSpacing,  tongueCount, tongueSamples, it);
    }

    pushTransform(pg);
    pg.beginShape(TRIANGLE_STRIP);
      for (int j = 0; j < vertices.size()-tongueSamples; j++){

        // Grab the first vertex in the array
        PVector p1 = vertices.get(j).position;

        // And the corresponding vertex on the next circle
        PVector p2 = vertices.get((j + tongueSamples)).position;

        // Add them to the buffer
        pg.vertex(p1.x, p1.y, p1.z);
        pg.vertex(p2.x, p2.y, p2.z);
      }
      pg.endShape();


    popTransform(pg);
  }

  // Draws a deformed circle depending on GUI specified values
  public void drawCircle(PVector v, float radius, float frequency, float magnitude, float seed, float s ){
    for (int i = 0; i < s; i++){
        float angle = (2 * PI * i) / s;

        float x = radius * sin(angle);
        float y = radius * cos(angle);

        float deformation = (float)simplexNoise.eval(x * frequency, y * frequency, seed) + 1;
        float z = radius * (1 + magnitude * deformation);
        float rad = radius * (1 + magnitude * deformation);

        pushVert(v.x + rad * x, v.y + rad * y, v.z + rad * z);
    }
  }


  public void drawTongue(PVector v, float radius, float frequency, float magnitude, float independence, float spacing, float count, float s, int time){

    // Adjust the radius so it's roughly the same size irregardless of magnitude
    radius /= (magnitude + 1);

    // Create a stack of circles
    for (int i = 0; i < count; i++) {

        // Draw the initial circle
        drawCircle(v, radius, frequency, magnitude, i * independence, s);

        // Create a scaled time value
        float t = 1.0f * time / numFrames_Slider;

        // Generate a looping noise value depending on the position of the circle in the stack
        float noiseX = (float)simplexNoise.eval(scale_Slider * (i*mod1), scale_Slider * (i*mod1), radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        float noiseY = (float)simplexNoise.eval(scale_Slider * (i*mod2), scale_Slider * (i*mod2), radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

        // Map the returned noise value to the GUI specified range
        v.x += map2(noiseX, -1, 1, -tongueAmount, tongueAmount, QUADRATIC, EASE_IN_OUT);
        v.y += map2(noiseY, -1, 1, -tongueAmount, tongueAmount, QUADRATIC, EASE_IN_OUT);

        // shrink the radius of the next circle
        radius *= (1 - spacing);

        // If the radius reaches the minimum, close the end of the shape
        if (radius <= 3) radius = 0;

        // Assign the value to the Z vertex
        v.z += tongueZ;

    }
  }
} // End of Tongue class
////////////////
// VERT CLASS //
////////////////

// This class is the core of the animations. Every vectors current position is modulated by creating
// a new value and adding that its original position. Every Vector stored in the array is a type of Vert,
// essentially two vectors added together.
//
// The most important variables for noise modulation are the 'scale' and 'radius' sliders. Scale changes
// the size of the area to draw noise from, radius increases and decreases the the speed of modulation by
// drawing smaller/larger circles in noise space.
//
// the Brownian noise modulator has it's own unique controls for amplitude, frequency and persistence.

class Vert{

  // PROPERTIES
  PVector initPosition;
  PVector position;

  // CONSTRUCTOR
  Vert(float x, float y, float z){

    // Set initial position according to the shape class setup()
    initPosition  = new PVector(x, y, z);

    // Create a copy of that position to modulate
    position = initPosition.copy();
  }

  // For every frame, add the modulated position to the initial position
  // These are to be uncommented for use. Some work well with some shapes, and not with others
  public void update(int i){

    position = PVector.add(initPosition, getSimplexMod(i) );
    // position = PVector.add(initPosition, ripples(i) );
    // position = PVector.add(initPosition, floorMod(i) );
    // position = PVector.add(initPosition, headMod(i) );
    // position = PVector.add(initPosition, bulgeMod(i) );
    // position = PVector.add(initPosition, sourMod(i) );
    // position = PVector.add(initPosition, depressing(i) );
    // position = PVector.add(initPosition, turbulenceMod(i) );
    // position = PVector.add(initPosition, brownianSimplex(i) );
    // position = PVector.add(initPosition, brownianRidged(i) );
    // position = PVector.add(initPosition, sheetsMod(i) );
    // position = PVector.add(initPosition, squidger(i) );
    // position = PVector.add(initPosition, sinkMod(i) );
  }

  // MODULATORS //

  // A basic noise modulator using the built in Perlin noise in Processing.
  public PVector getNoise(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    // Grab the desired number of frames from the GUI
    int numFrames = numFrames_Slider;

    // And create a scaled time value
    float t = 1.0f * i / numFrames;

    float radius = radius_Slider;
    float scale = scale_Slider;

    float nsX = noise(scale * position.x);
    float nsY = noise(scale * position.y);
    float nsZ = noise(scale * position.z);
    float ns = noise(radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float zPos = map(nsX, -1, 1, -z_Slider, z_Slider);
    float xPos = map(nsY, -1, 1, -x_Slider, x_Slider);
    float yPos = map(nsZ, -1, 1, -y_Slider, y_Slider);

    if (toggle_Y) res.y = yPos;
    if (toggle_X) res.x = xPos;
    if (toggle_Z) res.z = zPos;

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }


  // Create a looping noise formula using 4D Simplex noise
  // This algorithm draws a circle in noise space to enable the values to return to the beginning and loop.
  // This is the core basis for animation used in Simulacra

  // Looping Simplex Noise by Etienne Jacob - https://necessarydisorder.wordpress.com/2017/11/15/drawing-from-noise-and-then-making-animated-loopy-gifs-from-there/
  // An example GIF by Golan Levin can be found here;
  // https://giphy.com/gifs/loop-processing-noise-xUOxeU2ELSPeTbevle

  public PVector getSimplexMod(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    float t = 1.0f * i / numFrames_Slider;

    float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsX = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.z, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsY = (float)simplexNoise.eval(scale_Slider * position.y, scale_Slider * position.z, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
    // float nsZ = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

    if (animated){
      if (toggle_X) res.x = map2(ns, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.y = map2(ns, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Z) res.z = map2(ns, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      if (toggle_X) res.x = map(ns, -1, 1, -x_Slider, x_Slider);
      if (toggle_Y) res.y = map(ns, -1, 1, -y_Slider, y_Slider);
      if (toggle_Z) res.z = map(ns, -1, 1, -z_Slider, z_Slider);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;

  }

  // Ripple Mod
  // This modulator uses a combination of Simplex noise and the distance function to obscure the original noise pattern
  // Tends to work best on flat shapes such as Planes and Hyperbolic Paraboloids
  public PVector ripples(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;

    float scale = scale_Slider;

    float t = 1.0f * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod2);
    float xPos = map(ns, -1, 1, 0, mod1);
    float yPos = map(ns, -1, 1, 0, mod1);
    float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
    float result = (rippler + distance);

    if (animated){
      res.x = map2(sin(result), -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      res.y = map2(sin(result), -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
      res.z = map2(sin(result), -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      res.x = map2(sin(result), -1, 1, 0, x_Slider, QUADRATIC, EASE_IN_OUT);
      res.y = map2(sin(result), -1, 1, 0, y_Slider, QUADRATIC, EASE_IN_OUT);
      res.z = map2(sin(result), -1, 1, 0, z_Slider, QUADRATIC, EASE_IN_OUT);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

// Turbulent Noise Mod
// Reference - https://lodev.org/cgtutor/randomnoise.html

  public PVector turbulenceMod(int i){

    // Create an empty vector
    PVector res = new PVector(0,0,0);

    // Create a scaled time value
    float t = 1.0f * i / numFrames_Slider;


    float n = map(t, -1, 1, -mod2, mod2);
    float value = 0;
    float size = 12;
    float initialSize = size;
    float radius = radius_Slider;
    float scale = scale_Slider;

    // Calculate turbulence
    while (size > 1){
      value += (float)simplexNoise.eval(position.x * scale, position.y * scale, position.z / (mod1/10) + ((mod2/10) * n),radius * cos(TWO_PI * n)) * size;
      size /= 4.0f;
    }

    float result = (mod2/10) * value / initialSize;

    if (toggle_X) res.x += map(result, -1, 1, -x_Slider, x_Slider);
    if (toggle_Y) res.y += map(result, -1, 1, -y_Slider, y_Slider);
    if (toggle_Z) res.z += map(result, -1, 1, -z_Slider, z_Slider);

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

  // Squidge mod - A modulator for creating squishy interactions
  public PVector squidger(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;

    float t = 1.0f * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod2);
    float xPos = map(ns, -1, 1, -(mod1/5), (mod1/5));
    float yPos = map(ns, -1, 1, -(mod2/5), (mod2/5));
    float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
    float result = 100 / (rippler + distance);

    if (toggle_X) res.x += map(result, -1, 1, -(x_Slider/4), (x_Slider/4));
    if (toggle_Y) res.y += map(result, -1, 1, -(y_Slider/4), (y_Slider/4));
    if (toggle_Z) res.z += map(result, -1, 1, -(z_Slider/4), (z_Slider/4));

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }

  public PVector sinkMod(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;

    float t = 1.0f * i / numFrames;

    float poker = map(sin(t * 2.0f), -1, 1, mod1, mod1);
    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));
    float spiroDist = dist(res.x, res.y, position.x + map(sin(i * 0.2f), -1, 1, mod2, -mod2), position.y + map(cos(i * 0.2f), -1, 1, -mod2, mod2));
    float result = 100 / (poker + spiroDist);

    if (toggle_X) res.x = map(result, 0, 25, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0, 25, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0, 25, -z_Slider, z_Slider);

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
}

// Brownian Simplex Modulator
// Reference - https://cmaher.github.io/posts/working-with-simplex-noise/

public PVector brownianSimplex(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int num_iterations = numIterations_Slider;
  int numFrames = numFrames_Slider;
  float amp = amp_Slider;
  float maxAmp = 0;
  float freq = freq_Slider;
  float persistence = persistence_Slider;
  float radius = radius_Slider;
  float t = 1.0f * i / numFrames;

  //add successively smaller, higher-frequency terms
     for(i = 0; i < num_iterations; i++){
       float ns = (float)simplexNoise.eval(position.x * freq, position.y * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;

       if (animated){
         res.z += map2(ns, -1, 1, zMin/10, zMax/10, QUADRATIC, EASE_IN_OUT);
         res.x += map2(ns, -1, 1, xMin/10, xMax/10, QUADRATIC, EASE_IN_OUT);
         res.y += map2(ns, -1, 1, yMin/10, yMax/10, QUADRATIC, EASE_IN_OUT);
       }
       else {
         res.z += map(ns, -1, 1, 0, z_Slider);
         res.x += map(ns, -1, 1, 0, x_Slider);
         res.y += map(ns, -1, 1, 0, y_Slider);
       }

       maxAmp += amp;
       amp *= persistence;
       freq *= 2;
    }
     //take the average value of the iterations
     res.z /= maxAmp;

     //normalize the result
     if (toggle_X) res.x = res.x * (255 - 0) / 2 + (255 + 0) / 2;
     if (toggle_Y) res.y = res.y * (255 - 0) / 2 + (255 + 0) / 2;
     if (toggle_Z) res.z = res.z * (255 - 0) / 2 + (255 + 0) / 2;

     // Advance the iterator
     i++;

     // Return the modulated value
     return res;

}

// Brownian Ridged Modulator
// An extensive of the Brownian noise mod.
// Reference - https://cmaher.github.io/posts/working-with-simplex-noise/
public PVector brownianRidged(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int num_iterations = numIterations_Slider;
  int numFrames = numFrames_Slider;
  float amp = amp_Slider;
  float maxAmp = 0;
  float freq = freq_Slider;
  float persistence = persistence_Slider;
  float radius = radius_Slider;
  float t = 1.0f * i / numFrames;

  //add successively smaller, higher-frequency terms, with a different noise value for each axis
     for(i = 0; i < num_iterations; i++){
         res.z += simplexNoise.eval(position.x * freq, position.y * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         res.x += simplexNoise.eval(position.x * freq, position.z * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         res.y += simplexNoise.eval(position.y * freq, position.z * freq, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t)) * amp;
         maxAmp += amp;
         amp *= persistence;
         freq *= 2;
       }

     // Average the iterations
     res.z /= maxAmp;

     // Normalise the result
     if (toggle_X) res.x = abs(map(res.x, -1, 1, 0, x_Slider));
     if (toggle_Y) res.y = abs(map(res.y, -1, 1, 0, y_Slider));
     if (toggle_Z) res.z = abs(map(res.z, -1, 1, 0, z_Slider));

     // Advance the iterator
     i++;

     // Return the modulated Vector
     return res;

}

public PVector sheetsMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;

  float scale = scale_Slider;

  float t = 1.0f * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(ns, -1, 1, -mod2, mod2);
  float xPos = map(ns, -1, 1, 0, x_Slider);
  float yPos = map(ns, -1, 1, 0, y_Slider);
  float distance = dist(res.x, res.y, position.x / xPos, position.y / yPos);
  float result = (mod1 * 10) / (rippler + distance);

  if (animated){
    if (toggle_X) res.x = map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y = map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Z) res.z = map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, -1, 1, 0, x_Slider);
    if (toggle_Y) res.y = map(result, -1, 1, 0, y_Slider);
    if (toggle_Z) res.z = map(result, -1, 1, 0, z_Slider);
  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

public PVector floorMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,50,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;
  float scale = scale_Slider;
  float t = 1.0f * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(sin(t), -1, 1, 170, 85);

  float rad = map(sin(t), -1, 1, -mod1, mod1);
  float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
  float noiseDist = dist(res.x, res.y, position.x + map(ns, -1, 1, -rad * 2, rad * 2), position.y + map(ns, -1, 1, rad * 2, -rad * 2));
  float result = 100 / (rippler + spiroDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x += map(result, -1, 1, x_Slider, 0);
    if (toggle_Y) res.y += map(result, -1, 1, y_Slider, 0);
    if (toggle_Z) res.z += map(result, -1, 1, z_Slider, 0);
  }


  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

public PVector headMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  int numFrames = numFrames_Slider;
  float radius = radius_Slider;
  float scale = scale_Slider;
  float t = 1.0f * i / numFrames;

  float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

  float rippler = map(ns, -1, 1, -10, 10);
  float rad = map(sin(ns), -1, 1, -1, 1);
  float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
  float noiseDist = dist(res.x, res.y, position.x / map(ns, -1, 1, -rad/2, rad/2), position.y / map(ns, -1, 1, rad/2, -rad/2));
  float result = 300 / (rippler + noiseDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, -1, 1, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, -1, 1, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, -1, 1, -z_Slider, z_Slider);

  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;


}

public PVector sourMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  float t = 1.0f * i / numFrames_Slider;

  // Generate a looping noise value
  float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));

  float poker = map(ns, -1, 1, mod1,mod2);
  float rad = map(ns, -1, 1, -10, 10);
  float noiseDist = dist(res.x, res.y, position.x + ns , position.y + ns);
  float result = 128.0f * ns / noiseDist;

  if (animated){
    if (toggle_X) res.x = map2(result, 0.8f, 2, xMin/20, xMax/20, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y = map2(result, 0.8f, 2, yMin/20, yMin/20, QUADRATIC, EASE_IN_OUT);
    if (toggle_Z) res.z = map2(result, 0.8f, 2, zMin/20, zMin/20, QUADRATIC, EASE_IN_OUT);
  }
  else{
    if (toggle_X) res.x = map(result, 0.8f, 2, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0.8f, 2, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0.8f, 2, -z_Slider, z_Slider);
  }

  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

public PVector bulgeMod(int i){

  // Create an empty Vector
  PVector res = new PVector(0,0,0);

  float t = 1.0f * i / numFrames_Slider;

  float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
  float poker = map(sin(ns), -1, 1, 0, mod2);
  float centreDist = dist(position.x + map(ns, -1, 1, -mod1, mod1), position.y + map(ns, -1, 1, -mod1, mod1), res.x, res.y);
  float result = 100 / (poker + centreDist);

  if (animated){
    if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
    if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
  }
  else if (!animated){
    if (toggle_X) res.x = map(result, 0, 3, -x_Slider, x_Slider);
    if (toggle_Y) res.y = map(result, 0, 3, -y_Slider, y_Slider);
    if (toggle_Z) res.z = map(result, 0, 3, -z_Slider, z_Slider);
  }
  // Advance the iterator
  i++;

  // Return the modulated Vector
  return res;
}

public PVector depressing(int i){

    // Create an empty Vector
    PVector res = new PVector(0,0,0);

    int numFrames = numFrames_Slider;
    float radius = radius_Slider;
    float scale = scale_Slider;
    float t = 1.0f * i / numFrames;

    float ns = (float)simplexNoise.eval(scale * position.x, scale * position.y, radius * sin(TWO_PI * t), radius * cos(TWO_PI * t));

    float rippler = map(ns, -1, 1, 0, mod1);
    float rad = map(ns, -1, 1, -mod2, mod2);
    float spiroDist = dist(res.x, res.y, position.x + map(sin(t), -1, 1, -rad, rad), position.y + map(cos(t), -1, 1, rad, -rad));
    float noiseDist = dist(position.x, position.y, res.x + map(sin(ns), -1, 1, -rad, rad), res.y + map(cos(ns), -1, 1, -rad, rad));
    float result = 100 / (rippler + noiseDist);

    if (animated){
      if (toggle_Z) res.z += map2(result, -1, 1, zMin, zMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_X) res.x += map2(result, -1, 1, xMin, xMax, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.y += map2(result, -1, 1, yMin, yMax, QUADRATIC, EASE_IN_OUT);
    }
    else if (!animated){
      if (toggle_Z) res.z += map2(result, -1, 1, -z_Slider, z_Slider, QUADRATIC, EASE_IN_OUT);
      if (toggle_X) res.x += map2(result, -1, 1, -y_Slider, y_Slider, QUADRATIC, EASE_IN_OUT);
      if (toggle_Y) res.z += map2(result, -1, 1, -y_Slider, y_Slider, QUADRATIC, EASE_IN_OUT);
    }

    // Advance the iterator
    i++;

    // Return the modulated Vector
    return res;
  }

} // End of Vert class
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "simulacraMaster" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
