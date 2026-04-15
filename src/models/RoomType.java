package models;

public class RoomType  {
    private String typeName;
    private double basePrice;
    private Roomtypee type;

    public RoomType(String name, double price) {
        this.typeName = name;
        this.basePrice = price;
    }
public RoomType(Roomtypee typee){

       this.type=typee;
}
    public String getTypeName() { return this.typeName; }
    public double getBasePrice() { return this.basePrice; }
    public Roomtypee getRoomType(){return  this.type;}

    public void setType(Roomtypee type) {
        this.type = type;
    }
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString(){
        return type.toString();
    }
}