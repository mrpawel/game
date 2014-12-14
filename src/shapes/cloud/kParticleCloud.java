package shapes.cloud;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
/**
 * Created by user on 12/13/2014.
 */
public class kParticleCloud extends Kernel {
    static final int PARTICLES_MAX = 100000;
    static int numParticles=0;

    //PARTICLE PARAMS
    static final float[] vx = new float[PARTICLES_MAX]; //velocity_x
    static final float[] vy = new float[PARTICLES_MAX]; //velocity_y
    static final float[] vz = new float[PARTICLES_MAX]; //velocity_z

    static final float[] px = new float[PARTICLES_MAX]; //position_x
    static final float[] py = new float[PARTICLES_MAX]; //position_y
    static final float[] pz = new float[PARTICLES_MAX]; //position_z

    static final float[] pd = new float[PARTICLES_MAX]; //density
    static final float[] pm = new float[PARTICLES_MAX]; //mass
    static final float[] pp = new float[PARTICLES_MAX]; //pressure

    static final int MAX_NEIGHBORS = 20;
    static final int[] pn = new int[PARTICLES_MAX*MAX_NEIGHBORS]; //neighbors by index
    static final int[] pnn = new int[PARTICLES_MAX]; //neighbors totals by index

    static final float neighborDistance = 1.0f;

    public kParticleCloud(int _numParticles){
        numParticles=min(_numParticles, PARTICLES_MAX);
        generateParticles();
    }

    public void generateParticles(){
        for(int i=0; i<numParticles; i++){
            initParticle(i);
            resetNeighbors(i);
        }
    }

    public void initParticle(int i){
        setVelocity(i,0,0,0);
        setPosition(i,0,0,0);
        setMass(i,1f);
        setDensity(i,1f);
        setPressure(i,1f);
    }

    public void setVelocity(int i, float x, float y, float z){vx[i]=x; vy[i]=y; vz[i]=z;}
    public void setPosition(int i, float x, float y, float z){px[i]=x; py[i]=y; pz[i]=z;}
    public void setMass(int i, float x){pm[i]=x;}
    public void setDensity(int i, float x){pd[i]=x;}
    public void setPressure(int i, float x){pp[i]=x;}

    public void resetNeighbors(int i){
        pnn[i]=0;
        for(int n=0; n<MAX_NEIGHBORS; n++){
            pn[i*MAX_NEIGHBORS+n]=0;
        }
    }

    public void addNeighbor(int i, int n){
        if(pnn[i] < MAX_NEIGHBORS && !alreadyNeighbors(i,n)){
            pnn[i]++;
            pn[i*MAX_NEIGHBORS+pnn[i]]=n;
        }
    }

    public boolean alreadyNeighbors(int i, int n){
        for(int j=0; j<pnn[i]; j++){
            if(pn[i*MAX_NEIGHBORS+j]==n)return true;
        }
        return false;
    }

    public void findNeighbors(int i){
        resetNeighbors(i);
        for(int o=0; o<numParticles; o++){
            if(distance(o,i)<neighborDistance){
                addNeighbor(i,o);
            }
        }
    }

    public float distance(int i, int o){
        float x = px[i]-px[o];
        float y = py[i]-py[o];
        float z = pz[i]-pz[o];
        return sqrt(x*x+y*y+z*z);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void update(){

        long time1=System.currentTimeMillis();

        this.setExplicit(true);

        //update data w/:
        //this.put(lineXY1).put(lineZS1).put(lineDI)
        //        .put(lineXY2).put(lineZS2);

        Range range = Range.create(numParticles);

        this.execute(range, 3);
        //this.get(pixels);

        System.out.println(this.getExecutionMode() + " " + this.getExecutionTime());
        System.out.println("ran in " + (System.currentTimeMillis()-time1)+"ms " + numParticles);
    }

    @Override
    public void run() {
        //findNeighbors
        //updateParticleVelocities();
        //updateParticlePositions();
        int i = getGlobalId(0);
        int pass = getPassId();
    }
}
