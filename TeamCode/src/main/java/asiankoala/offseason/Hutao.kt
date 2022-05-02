package asiankoala.offseason

import asiankoala.offseason.rework.ComplexTurret
import asiankoala.offseason.subsystems.*
import com.asiankoala.koawalib.control.motion.MotionConstraints
import com.asiankoala.koawalib.logger.Logger
import com.asiankoala.koawalib.math.Pose
import com.asiankoala.koawalib.rework.ComplexMotorSettings
import com.asiankoala.koawalib.rework.SimpleComplexMotor
import com.asiankoala.koawalib.subsystem.drive.KMecanumOdoDrive

class Hutao(startPose: Pose) {
    private val hardware = Hardware()
    val encoders = Encoders(hardware)

    val drive = KMecanumOdoDrive(hardware.flMotor, hardware.blMotor, hardware.brMotor, hardware.frMotor, encoders.odo, true)
    val intake = Intake(hardware.intakeMotor, hardware.distanceSensor)
    val arm = Arm(hardware.armServo)
    val indexer = Indexer(hardware.indexerServo)
    val outtake = Outtake(hardware.outtakeServo)
    val duck = Duck(hardware.duckMotor)
    val turret = Turret(hardware.turretMotor)
    val slides = Slides(hardware.slideMotor)

    val complexTurret = ComplexTurret(
            SimpleComplexMotor(
                    ComplexMotorSettings(
                            "turret",
                            0.05,
                            0.0,
                            0.0,
                            5.33333,
                            false,
                    ),

                    0.0,
                    0.0,
                    0.0,

                    MotionConstraints(30.0, 30.0, 30.0),
                    1.0,
            ).brake as SimpleComplexMotor
    )

    fun log() {
        Logger.addTelemetryData("power", drive.powers.rawString())
        Logger.addTelemetryData("position", drive.pose)
        Logger.addTelemetryData("turret angle", turret.motor.encoder.pos)
        Logger.addTelemetryData("slides inches", slides.motor.encoder.pos)
    }

    init {
        drive.setStartPose(startPose)
        slides.motor.encoder.zero()
        turret.motor.encoder.zero(Turret.zeroAngle)
        slides.motor.followMotionProfile(0.0)
        turret.motor.setPIDTarget(Turret.homeAngle)
    }
}