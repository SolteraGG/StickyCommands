package event;

import com.dumbdogdiner.stickycommands.api.economy.Listing;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ListingCreateEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	@Getter
	@NotNull
	private Listing listing;

	@Getter
	@NotNull
	private OfflinePlayer seller;

	public ListingCreateEvent(@NotNull Listing listing) {
		this.listing = listing;
		this.seller = listing.seller;
	}

	/**
	 * @return True of this event is cancelled
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * Change the cancelled state of this event.
	 * @param cancel The new state
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@NotNull
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}
