# BlockPalette

![](http://i.imgur.com/iEtBnSG.gif)

## Accessing the Wheel
#### Keyboard Mode
- **ingame** - whilst holding an item, press and hold the blockpalette keybind (`c` by default)
- **creative menu** - whilst hovering the mouse over an itemstack, press and hold the blockpalette keybind

#### Mouse Mode
- **ingame** - whilst holding an item, type (don't hold) the blockpalette keybind (`c` by default).
- **creative menu** - click an item in the gui to open the palette GUI. Hold `shift` or `ctrl` or `c` to interact with the creative inventory normally

## Palette GUI Mouse Behaviours
- **Click** an item on the wheel to pick it up. Click again over a hotbar slot to add it to your inventory. Click anywhere else to drop it
- **Shift-click** an item on the wheel to set it as the center point and build a new palette around it
- **Right-click** items on the wheel to select multiple stacks that will be added to you inventory when you close the gui

## Palette GUI Settings
| Setting | Description |
|:----|:----|
|PickMode|Toggle between `Mouse Pickmode` & `Keyboard Pickmode`|
|Highlight Color|Adjust the RGB colouring of the highlight when hovering over itemsacks|
|Highlight Scale|Set how large the highlight should be|
| | |
|Color Mode|Toggle between the various color-picking modes|
|Opacity|Adjust the transparency of the background color shown on the Palette|
|Angle|Controls the portion of the color wheel that a given color will be picked from|
|Group Size|Controls the number of variations of a single color to be presented|
|Leniency|Controls how accurately the saturation of colors should be matched|

### Color Modes
The `Color Mode` determines a set of points on the color wheel from which colors/textures are looked up.  
The `Angle` setting determines how far from these starting points to actually look up on the color wheel.

See and play around with [paletton.com](http://paletton.com) for a great visual representation of these concepts.

| Mode | Description |
|:----|:-----|
|Complimentary|Find textures on the opposite side (180 degrees) of the color wheel to the center block|
|Adjacent|Find textures `Angle` degrees either side of the center block|
|Triad|Find textures 180 plus & minus `Angle` degrees away from the center block|
|Tetrad|Find textures `Angle` degrees away from the center block, and complimentary textures for it and the center block|
