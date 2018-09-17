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

        tempVec.x = i*planeRes - (planeWidth*scl/2.0);
        tempVec.y = j*planeRes - (planeHeight*scl/2.0);
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
