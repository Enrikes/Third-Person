package net.leawind.mc.thirdperson.impl.core.rotation;


import net.leawind.mc.thirdperson.ThirdPerson;
import net.leawind.mc.thirdperson.api.core.rotation.IRotateTarget;
import net.leawind.mc.thirdperson.core.CameraAgent;
import net.leawind.mc.util.api.math.LMath;
import net.leawind.mc.util.api.math.vector.Vector2d;
import net.leawind.mc.util.api.math.vector.Vector3d;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * 旋转目标，即玩家应该转向何处
 * TODO no t
 */
public enum RotateTarget implements IRotateTarget {
	/**
	 * 保持当前朝向，不旋转
	 */
	NONE(t -> ThirdPerson.ENTITY_AGENT.getRawRotation(1)),
	/**
	 * 与相机朝向相同
	 */
	CAMERA_ROTATION(t -> CameraAgent.calculateRotation()),
	/**
	 * 转向相机的视线落点，即准星所指的位置
	 */
	CAMERA_HIT_RESULT(t -> {
		Vector3d cameraHitPosition = CameraAgent.getPickPosition();
		if (cameraHitPosition == null) {
			return CAMERA_ROTATION.getRotation(t);
		} else {
			Minecraft mc = Minecraft.getInstance();
			assert mc.cameraEntity != null;
			Vector3d eyePosition = LMath.toVector3d(mc.cameraEntity.getEyePosition(t));
			Vector3d viewVector  = cameraHitPosition.sub(eyePosition);
			return LMath.rotationDegreeFromDirection(viewVector);
		}
	}),
	/**
	 * 使用键盘控制的移动方向
	 * <p>
	 * 当没有使用键盘控制时，则保持当前朝向
	 */
	IMPULSE_DIRECTION(t -> ThirdPerson.impulseHorizon.length() < 1e-5    //
						   ? NONE.getRotation(t)    //
						   : LMath.rotationDegreeFromDirection(ThirdPerson.impulse)),
	HORIZONTAL_IMPULSE_DIRECTION(t -> {
		if (ThirdPerson.impulseHorizon.length() < 1e-5) {
			return NONE.getRotation(t);
		} else {
			double absoluteYRotDegree = LMath.rotationDegreeFromDirection(ThirdPerson.impulseHorizon);
			return Vector2d.of(0, absoluteYRotDegree);
		}
	});
	private final Function<Float, Vector2d> rotationGetter;

	RotateTarget (@NotNull Function<Float, Vector2d> rotationGetter) {
		this.rotationGetter = rotationGetter;
	}

	/**
	 * 获取玩家当前的目标朝向
	 */
	@Override
	public @NotNull Vector2d getRotation (float partialTick) {
		return rotationGetter.apply(partialTick);
	}
}