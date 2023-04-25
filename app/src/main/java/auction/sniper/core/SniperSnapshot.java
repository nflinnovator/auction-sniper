package auction.sniper.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.objogate.exception.Defect;

public final class SniperSnapshot {

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
		LOST, WON, FAILED;

		public SniperState whenAuctionClosed() {
			throw new Defect("Auction is already closed");
		}
	}

	private final String itemId;
	private final int lastPrice;
	private final int lastBid;
	private final SniperState state;

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

	public SniperSnapshot failed() {
		return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
	}

	public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
		return this.itemId.equals(sniperSnapshot.itemId);
	}

	public String getItemId() {
		return itemId;
	}

	public int getLastPrice() {
		return lastPrice;
	}

	public int getLastBid() {
		return lastBid;
	}

	public SniperState getState() {
		return state;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
