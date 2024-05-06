package de.uniks.stp24.model;


import javax.inject.Inject;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Game
{
    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_PAUSED = "paused";

    private Boolean paused = false;

    @Inject
    public Game() {

    }

    public Boolean getPaused()
    {
        return this.paused;
    }

    public Game setPaused(Boolean value)
    {
        if (Objects.equals(value, this.paused))
        {
            return this;
        }

        final Boolean oldValue = this.paused;
        this.paused = value;
        this.firePropertyChange(PROPERTY_PAUSED, oldValue, value);
        return this;
    }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if (this.listeners != null)
        {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
            return true;
        }
        return false;
    }

    public PropertyChangeSupport listeners()
    {
        if (this.listeners == null)
        {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    @Override
    public String toString()
    {
        final StringBuilder result = new StringBuilder();
        result.append(' ').append(this.getPaused());
        return result.substring(1);
    }

    public void removeYou()
    {
        this.setPaused(null);
    }
}
