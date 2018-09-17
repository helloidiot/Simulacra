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
      float lon = map(i, 0, sphereResW, 0.0001, PI-0.0001);

      for (int j = 0; j < sphereResH+1; j++){
        // Create a laitude value according to the iterator
        float lat = map(j, 0, sphereResH, 0.0001, TWO_PI-0.0001);

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
          creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004, (it + (i*200)) * 0.005);
          creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004, (it + (i*300)) * 0.005);
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
