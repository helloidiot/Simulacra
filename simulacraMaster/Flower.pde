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
      float t = 1.0 * it / numFrames_Slider;
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
  void drawCircle(PVector v, float radius, float frequency, float magnitude, float seed, float s ){

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

  void drawFlower(PVector v, float radius, float frequency, float magnitude, float independence, float spacing, float count, float s){
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
