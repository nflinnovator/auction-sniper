package auction.sniper.core;

import java.util.Objects;

import com.objogate.exception.Defect;

public final class SniperSnapshot {

	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState state;

	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = state;
	}

	public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
		return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
	}

	public SniperSnapshot winning(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
	}
	
    public SniperSnapshot losing(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
    }

	public static SniperSnapshot joining(String itemId) {
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}

	public SniperSnapshot closed() {
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
	}

	public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
		return this.itemId.equals(sniperSnapshot.itemId);
	}

	public enum SniperState {
		JOINING {
			@Override
			public SniperState whenAuctionClosed() {
				return LOST;
			}
		},
		BIDDING {
			@Override
			public SniperState whenAuctionClosed() {
				return LOST;
			}
		},
		LOSING {
			@Override
			public SniperState whenAuctionClosed() {
				return LOST;
			}
		},
		WINNING {
			@Override
			public SniperState whenAuctionClosed() {
				return WON;
			}
		},
		LOST, WON;

		public SniperState whenAuctionClosed() {
			throw new Defect("Auction is already closed");
		}
	}

	@Override
	public String toString() {
		return "SniperState{" + "itemId='" + itemId + '\'' + ", lastPrice=" + lastPrice + ", lastBid=" + lastBid
				+ ", state=" + state.name() + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SniperSnapshot that = (SniperSnapshot) o;
		return lastPrice == that.lastPrice && lastBid == that.lastBid && Objects.equals(itemId, that.itemId)
				&& Objects.equals(state, that.state);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, lastPrice, lastBid, state);
	}

}
