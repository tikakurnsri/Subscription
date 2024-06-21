package Model;

public class SubscriptionItem {
    private int item;
    private int quantity;
    private double price;
    private double amount;
    private Item items;

    public SubscriptionItem() {}

    public SubscriptionItem(int itemId, int quantity, double price, double amount) {
        this.item = itemId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }

    public int getItem() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "SubscriptionItem{" +
                "item=" + itemId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", item=" + item +
                '}';
    }
}
