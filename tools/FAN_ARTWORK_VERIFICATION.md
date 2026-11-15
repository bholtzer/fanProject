# Animated fan artwork verification

The catalog uses realistic PNGs; the simulator uses scalable, stylized artwork. Each live design follows the corresponding image's silhouette, principal materials, mounting style, and visible blade count. It is not a photorealistic reproduction of the PNG.

- Default finishes now come from each fan design instead of global mint green. The original finish is selectable alongside custom colors and resolves on restore through the repository.
- Hand fans sway about the handle; the hanging punkah swings about its suspension.
- Rotors and LED arms move independently of fixed guards, stands, brackets, and motor housings.
- Tower, bladeless multiplier, and purifier display moving airflow only while running. Their slim proportions follow the actual generated images.
- Stopping the simulator preserves the current rotor/sway angle instead of jumping back to the manual angle.
- Customization's animated preview now moves as well.

## Validation

`gradle :androidApp:assembleDebug :shared:testDebugUnitTest --offline --console=plain`

Three tests cover all 21 catalog entries: selected identity and original finish, custom finish selection, and visible rotor counts.

Emulator comparison captures `fan-audit-0.png` through `fan-audit-6.png` show all 21 product designs beside the live artwork at phase 45. Alternate phase captures for pages 0, 1, and 5 verify hand sway, rotor movement, and airflow. The temporary comparison composable is retained as `FanArtworkAudit.kt.txt` for reproduction; it is not compiled into the app.

The normal app flow was also exercised: select Folding Hand Fan, confirm Ivory paper, start the simulator, and power on. `fan-main-running-a.png` and `fan-main-running-b.png` show different sway positions in the actual simulator.
