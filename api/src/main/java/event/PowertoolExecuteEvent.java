package event;

import com.dumbdogdiner.stickycommands.api.item.Powertool;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PowertoolExecuteEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private Powertool powertool;
	private String command;

	public PowertoolExecuteEvent(@NotNull Powertool powertool) {
		this.powertool = powertool;
		this.command = powertool.getCommand();
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

	/**
	 * Get the command being executed
	 * @return The command as a {@link String}
	 */
	@NotNull
	public String getCommand() {
		return this.command;
	}

	/**
	 * Get the powertool currently executing
	 * @return The powertool
	 */
	@NotNull
	public Powertool getPowertool() {
		return this.powertool;
	}
}
