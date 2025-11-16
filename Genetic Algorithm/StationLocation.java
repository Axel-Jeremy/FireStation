// Class untuk menyimpan koordinat dari 1 FireStation di grid peta

public class StationLocation {
    private int x; // Baris
    private int y; // Kolom

    // Konstruktor untuk membuat instance StationLocation baru.
    public StationLocation(int x, int y){
        this.x = x; // Baris
        this.y = y; // Kolom
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
    
    // Formating menuju String lagi agar mudah dibaca dan dipanggil nantinya
    @Override
    public String toString(){
        return String.format("(%d, %d)\n", this.x, this.y);
    }
}
