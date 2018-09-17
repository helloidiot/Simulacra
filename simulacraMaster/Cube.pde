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
                pushVert(i*spacing - (w*spacing/2.0), j*spacing - (h*spacing/2.0), k*spacing - (w*spacing/2.0));
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
