package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *  Represents a Cigar as an object
 */
public class Cigar {
    private StringProperty cigarId;
    private StringProperty userId;
    private StringProperty brand;
    private StringProperty price;

    public Cigar(String cigarId, String userId, String brand, String price) {
        this.cigarId = new SimpleStringProperty(cigarId);
        this.userId = new SimpleStringProperty(userId);
        this.brand = new SimpleStringProperty(brand);
        this.price = new SimpleStringProperty(price);
    }

    public void setCigarId(String cigarId) {
        this.cigarId.set(cigarId);
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }

    public void setBrand(String brand) {
        this.brand.set(brand);
    }

    public void setPrice(String price) {
        this.price.set(price);
    }

    public String getCigarId() {
        return cigarId.get();
    }

    public String getUserId() {
        return userId.get();
    }

    public String getBrand() {
        return brand.get();
    }

    public String getPrice() {
        return price.get();
    }

    public StringProperty cigarIdProperty() {
        return cigarId;
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public StringProperty brandProperty() {
        return brand;
    }

    public StringProperty priceProperty() {
        return price;
    }
}
