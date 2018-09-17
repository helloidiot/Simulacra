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

  void getVertices(PShape shape, ArrayList<Vert> verts){

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
