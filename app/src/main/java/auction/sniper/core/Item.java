package auction.sniper.core;

import java.util.Objects;

public final class Item {

	public final String identifier;
	public final int stopPrice;

	public Item(String identifier, int stopPrice) {
		this.identifier = identifier;
		this.stopPrice = stopPrice;
	}

	public boolean allowsBid(int bid) {
		return bid <= stopPrice;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Item item = (Item) o;
		return stopPrice == item.stopPrice && Objects.equals(identifier, item.identifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(identifier, stopPrice);
	}

}
