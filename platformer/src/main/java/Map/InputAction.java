package Map;

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
            return "Map.Lava";
        }

    };


    public static boolean isUnactionable(InputAction action) {
        return (action == InputAction.Default|| action == InputAction.Swim|| action == InputAction.Lava);
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
