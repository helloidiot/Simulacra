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
        temp.y = 0.16 * prevy;
      }
      // Pinna
      else if (rnd <= 7) { // 7%
          temp.x = 0.2 * prev.x - 0.26 * prev.y;
          temp.y = 0.23 * prev.x + 0.22 * prev.y + 1.6;
      }
      // Successively smaller leaflets
      else if (rnd <= 15) { // 85%
          temp.x = -0.15 * prev.x + 0.28 * prev.y;
          temp.y = 0.26 * prev.x + 0.24 * prev.y + 0.44;
      }
      // Pinna
      else { // the other 7%
          temp.x = 0.85 * prev.x + 0.04 * prev.y;
          temp.y = -0.04 * prev.x + 0.85 * prev.y + 1.6;
      }

      tempVec.x = 300 + temp.x * 108;
      tempVec.y = 800 - (temp.y * 108) * 0.7;
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
