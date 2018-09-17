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
          creatures[i].x = (float)simplexNoise.eval((it + (i*100)) * 0.004, (it + (i*200)) * 0.005);
          creatures[i].y = (float)simplexNoise.eval((it + (i*200)) * 0.004, (it + (i*300)) * 0.005);
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
      float t = 1.0 * it / numFrames_Slider;
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
