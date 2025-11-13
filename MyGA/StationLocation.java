public class StationLocation {
    private int x;
    private int y;

    public StationLocation(int x, int y){
        this.x = x;
        this.y = y;
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
    
    @Override
    public String toString(){
        return String.format("(%d, %d)\n", this.x, this.y);
    }
}
