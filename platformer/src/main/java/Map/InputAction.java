package Map;

import java.util.ArrayList;

public enum InputAction {
    Default() {
      public String toString() {
          return "Default";
      }
    },
    Right() {
        public String toString() {
            return "Right";
        }
    },
    Left() {
        public String toString() {
            return "Left";
        }
    },
    Up() {
        public String toString() {
            return "Up";
        }
    },
    Down() {
        public String toString() {
            return "Down";
        }
    },
    Sprint() {
        public String toString() {
            return "Sprint";
        }
    },
    Hook() {
        public String toString() {
            return "Hook";
        }
    },
    Menu() {
        public String toString() {
            return "Menu";
        }
    },
    FullScreen() {
        public String toString() {
            return "FullScreen";
        }
    },
    Reset() {
        public String toString() {
            return "Reset";
        }
    },
    StickyDown() {
        public String toString() {
            return "StickyDown";
        }
    },
    StickyUp() {
        public String toString() {
            return "StickyUp";
        }
    },
    StickyRight() {
        public String toString() {
            return "StickyRight";
        }
    },
    StickyLeft() {
        public String toString() {
            return "StickyLeft";
        }
    },
    Swim() {
        public String toString() {
            return "Swim";
        }
    },

    Control() {
        public String toString() {
            return "Control";
        }
    },
    Corner() {
        public String toString() {
            return "Corner";
        }

    },
    Lava() {
        public String toString() {
            return "Lava";
        }

    },
    Gear() {
        public String toString() {
            return "Gear";
        }
    },
        Shurikan() {
            public String toString() {
                return "Shurikan";
            }
    };



    public static boolean containsWall(ArrayList<InputAction> actions) {
        return (actions.contains(InputAction.Down)
                || actions.contains(InputAction.Right)
                || actions.contains(InputAction.Left)
                || actions.contains(InputAction.Up)
                || actions.contains(InputAction.StickyDown)
                || actions.contains(InputAction.StickyLeft)
                || actions.contains(InputAction.StickyRight)
                || actions.contains(InputAction.StickyUp)
                || actions.contains(InputAction.Corner));
    }


    public static boolean isUnactionable(InputAction action) {
        return (action == InputAction.Default|| action == InputAction.Swim|| action == InputAction.Lava|| action == InputAction.Gear);
    }

    public static boolean isRightType(InputAction action) {
        return (action == InputAction.Right|| action == InputAction.StickyRight);
    }
    public static boolean isLeftType(InputAction action) {
        return (action == InputAction.Left || action == InputAction.StickyLeft);
    }

    public static boolean isDownType(InputAction action) {
        return (action == InputAction.Down || action == InputAction.StickyDown);
    }

    public static boolean isUpType(InputAction action) {
        return (action == InputAction.Up|| action == InputAction.StickyUp);
    }


    public static boolean isXType(InputAction action) {
        return(action == InputAction.Right || action == InputAction.Left || action == InputAction.Corner);

    }

    public static boolean isYType(InputAction action) {
        return(action == InputAction.Down || action == InputAction.Up || action == InputAction.Corner);

    }

    public static boolean isWallType(InputAction action) {
        return(action == InputAction.Right || action == InputAction.Left|| action == InputAction.Up || action == InputAction.Down || action == InputAction.Corner);

    }
}
