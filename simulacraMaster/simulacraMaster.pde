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
import peasy.*;
import controlP5.*;
import nervoussystem.obj.*;
import java.util.*;
import toxi.sim.grayscott.*;
import toxi.math.*;

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
color lightBlue = color(154,227,221);
color palePink = color(232,194,202);
color orange = color(255,163,99);
color paleGrey = color(183,191,204);

// Assign viewport settings
int viewport_w = 1280;
int viewport_h = 720;

void settings(){
  size(viewport_w, viewport_h, P3D);
  smooth(0);
}

void setup() {

  // Cap frameRate so animations behave as expected
  // Animations are intended to be rendered at 23.98fps / 24fps.
  // Can cause runtime exception when using a class that requires some time to load (model, modelSequence)
  frameRate(24);

  // Initialise noise and GUI
  simplexNoise = new OpenSimplexNoise();
  gui = new GUI();

  // Initialise the Reaction Diffusion algorithm with initial values.
  gs = new GrayScott(gsWidth, gsHeight, true);
  gs.setCoefficients(0.028, 0.084, 0.095, 0.03);
  bReaction = false;
  bCreatures = false;

  // Setup PeasyCam
  cam = new PeasyCam(this, 200);
  cam.setMinimumDistance(0.0001);
  cam.setMaximumDistance(10000);
  if (bTrackpad) cam.setWheelScale(.05);
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

void draw() {

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

void drawAxis() {
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

void drawGrid() {
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
float map2(float value, float start1, float stop1, float start2, float stop2, int type, int when) {

  float b = start2;
  float c = stop2 - start2;
  float t = value - start1;
  float d = stop1 - start1;
  float p = 0.5;
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
    rotationX = 0.0;
    rotationY = 0.0;
    rotationZ = 0.0;

    shape1.rotation.x = 0.0;
    shape1.rotation.y = 0.0;
    shape1.rotation.z = 0.0;

    notification = "ROTATION RESET.";
}

public void resetScale(int theValue) {
    scaleMod = 1.0;

    shape1.scale.x = 1.0;
    shape1.scale.y = 1.0;
    shape1.scale.z = 1.0;

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
    cp5.getController("x_Slider").setValue(0.0);
    cp5.getController("xMin").setValue(0.0);
    cp5.getController("xMax").setValue(0.0);
    cp5.getController("y_Slider").setValue(0.0);
    cp5.getController("yMin").setValue(0.0);
    cp5.getController("yMax").setValue(0.0);
    cp5.getController("z_Slider").setValue(0.0);
    cp5.getController("zMin").setValue(0.0);
    cp5.getController("zMax").setValue(0.0);
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
    gs.setF(0.02+(key-'1')*0.001);
    println("F: " + gs.getF());
    notification = "GS K: " + str(gs.getF());
  }
  if (keyCode == UP) {
    gs.setK(gs.getK()+0.001);
    println("K: " + gs.getK());
    notification = "GS K: " + str(gs.getK());
  }
  if (keyCode == DOWN) {
    gs.setK(gs.getK()-0.001);
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

void push(){
  pushStyle();
  pushMatrix();
}

void pop(){
  popStyle();
  popMatrix();
}

void setStroke(){
  strokeWeight(stroke_Weight);
  stroke(stroke_Colour);
}
