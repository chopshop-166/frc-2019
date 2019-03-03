package com.mach.LightDrive;

import edu.wpi.first.wpilibj.*;

public final class LightDrivePWM
{
    private DigitalOutput m_pwm_select;
    private DigitalOutput m_pwm_value;
    private Servo m_servo_bank1;
    private Servo m_servo_bank2;
    private int[] m_matrix;
    private boolean m_type_servo;
    
    public LightDrivePWM(final Servo bank1, final Servo bank2) {
        this.m_servo_bank1 = bank1;
        this.m_servo_bank2 = bank2;
        this.m_matrix = new int[12];
        this.m_type_servo = true;
    }
    
    public void Update() {
        double newduty = 0.0;
        int channels = ((this.m_matrix[0] > 0) ? 16 : 0) | ((this.m_matrix[1] > 0) ? 32 : 0) | ((this.m_matrix[2] > 0) ? 64 : 0) | ((this.m_matrix[3] > 0) ? 128 : 0) | ((this.m_matrix[4] > 0) ? 256 : 0) | ((this.m_matrix[5] > 0) ? 512 : 0);
        newduty = channels / 1023.0;
        if (this.m_type_servo) {
            this.m_servo_bank1.set(newduty);
        }
        else {
            this.m_pwm_select.updateDutyCycle(newduty);
        }
        channels = (((this.m_matrix[6] > 0) ? 16 : 0) | ((this.m_matrix[7] > 0) ? 32 : 0) | ((this.m_matrix[8] > 0) ? 64 : 0) | ((this.m_matrix[9] > 0) ? 128 : 0) | ((this.m_matrix[10] > 0) ? 256 : 0) | ((this.m_matrix[11] > 0) ? 512 : 0));
        newduty = channels / 1023.0;
        if (this.m_type_servo) {
            this.m_servo_bank2.set(newduty);
        }
        else {
            this.m_pwm_select.updateDutyCycle(newduty);
        }
    }
    
    public void SetColor(int ch, final Color color) {
        if (ch < 1 || ch > 4) {
            return;
        }
        ch = --ch * 3;
        this.m_matrix[ch] = color.green;
        this.m_matrix[ch + 1] = color.red;
        this.m_matrix[ch + 2] = color.blue;
    }
    
    public void SetLevel(final int ch, final int level) {
        if (ch < 1 || ch > 12 || level < 0 || level > 255) {
            return;
        }
        this.m_matrix[ch] = level;
    }
}
