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
} // End of Model class
