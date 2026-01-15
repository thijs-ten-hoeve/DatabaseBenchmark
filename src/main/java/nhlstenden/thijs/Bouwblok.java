package nhlstenden.thijs;

public class Bouwblok {


    protected long id;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    protected String type;
    protected int width;

    protected int depth;
    protected int height;
    protected int rotation;

    protected double oppervlakte;
    protected double inhoud;
    protected double kosten;
    protected double opbrengst;
    protected double inwoners;
    protected double leefbaarheidsscore;

    protected int x;

    protected int y;

    protected int z;

    public Bouwblok(String type, int x, int y, int z, int width, int depth, int height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.depth = depth;
        this.height = height;
        this.rotation = 0;
        this.oppervlakte = width * depth * 275000 / 216545.23674674143;
        this.inhoud = this.oppervlakte * height;
        this.kosten = this.inhoud * 300;
        this.opbrengst = Math.round(this.kosten * 1.12);
        this.inwoners = Math.round(this.inhoud * 0.006);
        this.leefbaarheidsscore = 5;
    }


    public Bouwblok() {}

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getDepth() {
        return depth;
    }


    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public double getOppervlakte() {
        return oppervlakte;
    }

    public void setOppervlakte(double oppervlakte) {
        this.oppervlakte = oppervlakte;
    }

    public double getInhoud() {
        return inhoud;
    }


    public void setInhoud(double inhoud) {
        this.inhoud = inhoud;
    }

    @Override
    public String toString() {
        return "Bouwblok{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", width=" + width +
                ", depth=" + depth +
                ", height=" + height +
                ", rotation=" + rotation +
                ", oppervlakte=" + oppervlakte +
                ", inhoud=" + inhoud +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public double getKosten() {
        return kosten;
    }

    public void setKosten(double kosten) {
        this.kosten = kosten;
    }

    public double getLeefbaarheidsscore() {
        return leefbaarheidsscore;
    }

    public void setLeefbaarheidsscore(double leefbaarheidsscore) {
        this.leefbaarheidsscore = leefbaarheidsscore;
    }

    public double getInwoners() {
        return inwoners;
    }

    public void setInwoners(double inwoners) {
        this.inwoners = inwoners;
    }

    public double getOpbrengst() {
        return opbrengst;
    }

    public void setOpbrengst(double opbrengst) {
        this.opbrengst = opbrengst;
    }
}
