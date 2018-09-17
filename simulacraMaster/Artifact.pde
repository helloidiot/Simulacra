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
    scale = new PVector(1.,1.,1.);
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
        float t = 1.0 * it / numFrames_Slider;
        float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        scale_Slider = map2(ns, -1, 1, scaleMin, scaleMax, QUADRATIC, EASE_IN_OUT);
        radius_Slider = map2(ns, -1, 1, radiusMin, radiusMax, QUADRATIC, EASE_IN_OUT);
      }
      // Animate Simplex noise values based on a sinewave of a returned looping noise value.
      else if (sineN){
        float t = 1.0 * it / numFrames_Slider;
        float ns = (float)simplexNoise.eval(scale_Slider * position.x, scale_Slider * position.y, radius_Slider * sin(TWO_PI * t), radius_Slider * cos(TWO_PI * t));
        scale_Slider = map2(sin(ns), -1, 1, scaleMin, scaleMax, QUADRATIC, EASE_IN_OUT);
        radius_Slider = map2(sin(ns), -1, 1, radiusMin, radiusMax, QUADRATIC, EASE_IN_OUT);
      }
      // Animated Simplex Noise values based on a sine wave
      else if (sine){
        float t = 4.0 * it / numFrames_Slider;
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
