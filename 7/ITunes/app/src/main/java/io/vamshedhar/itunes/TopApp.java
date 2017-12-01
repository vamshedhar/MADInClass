package io.vamshedhar.itunes;

import android.support.annotation.NonNull;

import java.io.Serializable;


/**
 * Created by Vamshedhar Reddy Chintala (800988045) on 10/23/17 6:43 PM.
 * vchinta1@uncc.edu
 */

public class TopApp implements Serializable, Comparable<TopApp> {
    String id, name, price, imageUrl, thumbUrl;

    public TopApp() {
    }

    public TopApp(String id, String name, String price, String imageUrl, String thumbUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopApp topApp = (TopApp) o;

        return id != null ? id.equals(topApp.id) : topApp.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TopApp{" +
                "name='" + id + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull TopApp topApp) {
        double currentPrice = Double.parseDouble(this.price);
        double otherPrice = Double.parseDouble(topApp.price);
        if(currentPrice < otherPrice){
            return -1;
        } else if (currentPrice > otherPrice){
            return 1;
        }
        return 0;
    }
}
