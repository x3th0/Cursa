package club.eridani.cursa.event;

public class CursaEvent {

    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void cancel(){
        cancelled = true;
    }

}
