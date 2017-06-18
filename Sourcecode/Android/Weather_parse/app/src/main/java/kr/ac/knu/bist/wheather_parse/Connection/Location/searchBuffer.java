package kr.ac.knu.bist.wheather_parse.Connection.Location;

/**
 * Created by BIST120 on 2017-05-26.
 */
public class searchBuffer {

    private double searchX;
    private double searchY;
    private String address;

    public searchBuffer(double searchX, double searchY, String address){
        this.searchX=searchX;
        this.searchY=searchY;
        this.address=address;
    }

    public double getSearchX() {
        return searchX;
    }
    public double getSearchY() {
        return searchY;
    }
    public String getAddress(){
        return address;
    }

}

