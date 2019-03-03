package com.mach.LightDrive;

public class Status
{
    private byte m_raw;
    private mode m_mode;
    
    public Status() {
        this.m_raw = 0;
        this.m_mode = mode.NONE;
    }
    
    public byte GetTripped() {
        return (byte)((this.m_raw & 0xF0) >> 4);
    }
    
    public Boolean IsEnabled() {
        if ((this.m_raw & 0x1) > 0) {
            return true;
        }
        return false;
    }
    
    public mode GetMode() {
        return this.m_mode;
    }
    
    public Byte GetRaw() {
        return this.m_raw;
    }
    
    public void SetRaw(final byte raw) {
        this.m_raw = raw;
    }
    
    public enum mode
    {
        NONE("NONE", 0), 
        IDLE("IDLE", 1), 
        PWM("PWM", 2), 
        CAN("CAN", 3), 
        SERIAL("SERIAL", 4);
        
        private mode(final String s, final int n) {
        }
    }
}
