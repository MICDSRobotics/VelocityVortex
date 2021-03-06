/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.qualcomm.ftccommon.FtcRobotControllerService;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.inputtracking.Input;
import org.firstinspires.ftc.teamcode.inputtracking.InputWriter;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.teamcode.hardware.MotorPair;
import org.firstinspires.ftc.teamcode.hardware.Robot;
import org.firstinspires.ftc.teamcode.hardware.TankDrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Recorder", group="Shadow")  // @Autonomous(...) is the other common choice
public class Recording extends OpMode implements Playback
{
    /* Declare OpMode members. */

    private ElapsedTime runtime = new ElapsedTime();
    private Robot bot = null;

    private TankDrive tankDrive;
    private MotorPair leftMotors;
    private MotorPair rightMotors;

    private ArrayList<Input> inputs;
    private File file = new File(hardwareMap.appContext.getFilesDir(), Playback.INPUTS_RED);

    FileOutputStream outputStream;

    Looper looper;

    public Looper getLooper() {
        return looper;
    }

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {

        bot = new Robot(hardwareMap);

        inputs = new ArrayList<Input>();

        tankDrive = bot.getTankDrive();
        leftMotors = bot.getTankDrive().getLeftMotors();
        rightMotors = bot.getTankDrive().getRightMotors();

        telemetry.addData("Status", "Initialized");

    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Running: " + runtime.toString());

        leftMotors.setPower(gamepad1.left_stick_y);
        rightMotors.setPower(gamepad1.right_stick_y);

        if(gamepad1.a){
            bot.getLauncher().fullRotation();
        }

        if (gamepad1.b) {
            //bot.getLifter().stop();
            bot.getIntake().stop();
        }

        if (gamepad1.dpad_left) {
            bot.getIntake().takeInBall();
        }

        if (gamepad1.dpad_right) {
            bot.getIntake().purgeBall();
        }

        if (gamepad1.dpad_up) {
            //bot.getLifter().ascend();
        }

        if (gamepad1.dpad_down) {
            //bot.getLifter().descend();
        }

        if(gamepad1.left_bumper){
            bot.getSlider().setPower(1);
        } else if (gamepad1.right_bumper){
            bot.getSlider().setPower(-1);
        } else {
            bot.getSlider().setPower(0);
        }

        inputs.add(new Input(gamepad1, runtime.time()));

    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {

        try {
            outputStream = hardwareMap.appContext.openFileOutput(Playback.INPUTS_RED, Context.MODE_PRIVATE);
            InputWriter writer = new InputWriter();
            writer.writeJson(outputStream, inputs);
            telemetry.addData("Output", outputStream);
        } catch (IOException error){
            error.printStackTrace();
        }

        bot.stopMoving();
        bot.getTankDrive().resetEncoders();

    }

    @Override
    public void read(){}

}
