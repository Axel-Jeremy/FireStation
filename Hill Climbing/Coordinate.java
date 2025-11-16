// Class berfungsi sebagai struktur data yang akan menyimpan koordinat (x,y) dan jarak (distance) untuk keperluan BFS

public class Coordinate {
    private int x;
    private int y;
    private int distance;

    // Konstruktor untuk keperluan queue dari BFS
    public Coordinate(int x, int y, int distance) {
        this.x = x; // Koordinat x (Baris)
        this.y = y; // Koordinat y (Kolom)
        this.distance = distance;
    }

    // Konstruktor untuk menyimpan lokasi
    public Coordinate(int x, int y) {
        this.x = x; // Koordinat x (Baris)
        this.y = y; // Koordinat y (Kolom)
    }

    // Getter dan Setter
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
