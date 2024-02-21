# SplashFox
A mod that displays a bouncy blobfox on Minecraft loading screen, because yes.  

## Config
A GUI configuration screen is available on 1.19.3 and above by installing Mod Menu.  
You may also modify the config file manually under `Your_Game_Instances/config/splashfox/config.json` in circumstances where the Config GUI does not work.  
Configurable item as follows:  

| Item        | Description                                                        | Default                             | Type                                                                                        |
|-------------|--------------------------------------------------------------------|-------------------------------------|---------------------------------------------------------------------------------------------|
| Drop Height | How much the fox will drop before bouncing up again                | 1.5x                                | double                                                                                      |
| Fox Size    | Size of the fox                                                    | 1.5x                                | double                                                                                      |
| Speed       | Speed of the animation                                             | 1x                                  | double                                                                                      |
| Flipped     | Whether to flip the blobfox horizontally                           | false                               | boolean                                                                                     |
| Wobbly      | Whether the fox will deform slightly when hitting the lowest point | true                                | boolean                                                                                     |
| Image Path  | The Minecraft id reference to the image file                       | splashfox:textures/gui/ blobfox.png | string                                                                                      |
| Position    | Position of where the fox should be placed                         | ABOVE_MOJANG                        | Position:   LEFT_TO_MOJANG   RIGHT_TO_MOJANG   ABOVE_MOJANG   REPLACE_MOJANG   FOLLOW_MOUSE |

## Bugs/Suggestions
If you have any suggestions or bug report, don't hesitate to open an GitHub issue [here](https://github.com/Kenny-Hui/Splashfox/issues)!

## License
The blobfox emojis are licensed under Apache 2.0.  
This project is licensed under the MIT License.