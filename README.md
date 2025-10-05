# Flappy Bird Android

A classic Flappy Bird clone built for Android with custom graphics, sound effects, high score tracking, and a complete menu system.

## Features

- **Main Menu**: Clean interface with Play, High Scores, and Settings buttons
- **Gameplay**: Classic Flappy Bird mechanics with smooth physics
- **High Score System**: Track top 10 scores with player initials
- **Audio**: Background music and sound effects with independent volume controls
- **Settings**: Persistent settings for music and sound effects using SharedPreferences
- **Visual Enhancements**: Animated bird with flapping wings
- **Multiple Activities**: Organized app structure with separate screens for game, menu, settings, and high scores

## Screenshots

_Coming soon_

## Tech Stack

- **Language**: Java
- **Build System**: Gradle
- **Target SDK**: Android 34 (Android 14)
- **Minimum SDK**: Android 24 (Android 7.0)
- **Architecture**: Custom SurfaceView-based game engine
- **Audio**: MediaPlayer (music), SoundPool (sound effects)
- **Persistence**: SharedPreferences for settings and high scores

## Development Environment

This project uses a **Docker-based development environment** to ensure consistency across different systems. The Docker container includes:
- Ubuntu 22.04 base
- OpenJDK 17
- Android SDK (Platform 34, Build Tools 34.0.0)
- Platform tools (ADB)

### Prerequisites

- **Docker**: Install from [docker.com](https://www.docker.com/get-started)
- **Git**: For cloning the repository
- **Linux Host** (Recommended): Tested on Ubuntu 22.04
  - Windows/macOS users may need to adjust USB device mounting for ADB
- **Android Device** (Optional): For physical device testing
  - USB debugging enabled (see [Device Setup](#device-setup) below)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/jdpmurphy/flappy-bird-android.git
   cd flappy-bird-android
   ```

2. **Navigate to the Docker environment directory**:
   ```bash
   cd /path/to/android-dev-environment
   ```

3. **Build the Docker environment**:
   ```bash
   ./dev.sh setup
   ```
   This will build the Docker image with all necessary Android development tools.

4. **Start the development environment**:
   ```bash
   ./dev.sh start
   ```
   This launches an interactive Docker container with the project workspace mounted.

### Development Workflow

The `dev.sh` script provides convenient commands for common tasks:

```bash
# Setup and initialization
./dev.sh setup                    # Build Docker environment
./dev.sh start                    # Start interactive development session
./dev.sh clone <repo-url>         # Clone a GitHub repository

# Building
./dev.sh build                    # Build the project
./dev.sh debug                    # Build debug APK
./dev.sh clean                    # Clean build artifacts

# Device management
./dev.sh devices                  # List connected devices
./dev.sh install                  # Install APK to connected device
./dev.sh logs                     # View device logs (logcat)

# Environment management
./dev.sh shell                    # Open shell in running container
./dev.sh stop                     # Stop the container
./dev.sh destroy                  # Remove container and image (keeps source code)
```

**Example workflow**:
```bash
# First time setup
./dev.sh setup
./dev.sh start

# Inside the container, or from host:
./dev.sh debug                    # Build APK
./dev.sh devices                  # Check device connection
./dev.sh install                  # Install to device
```

### Device Setup

To test on a physical Android device:

1. **Enable Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Developer Options will appear in Settings

2. **Enable USB Debugging**:
   - Settings → Developer Options → Enable "USB Debugging"

3. **Connect device via USB** and verify connection:
   ```bash
   ./dev.sh devices
   ```
   You should see your device listed. If prompted on your phone, authorize the computer.

4. **Grant permissions** if prompted on device to allow USB debugging from your computer.

## Project Structure

```
flappy-bird-android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yourname/flappybird/
│   │   │   │   ├── MainActivity.java           # Main menu entry point
│   │   │   │   ├── GameActivity.java           # Game activity wrapper
│   │   │   │   ├── GameView.java               # Core game logic and rendering
│   │   │   │   ├── Bird.java                   # Bird entity with physics
│   │   │   │   ├── Pipe.java                   # Pipe obstacle entity
│   │   │   │   ├── HighScoresActivity.java     # High scores display
│   │   │   │   ├── HighScoreManager.java       # High score persistence
│   │   │   │   ├── HighScore.java              # High score data model
│   │   │   │   └── SettingsActivity.java       # Settings screen
│   │   │   ├── assets/
│   │   │   │   ├── sounds/
│   │   │   │   │   ├── music/
│   │   │   │   │   │   └── main_theme.mp3      # Background music (placeholder)
│   │   │   │   │   └── sfx/
│   │   │   │   │       └── player/
│   │   │   │   │           └── coin_pickup.wav # Score sound effect
│   │   │   │   └── sprites/
│   │   │   │       └── bird/
│   │   │   │           └── bird_sprite_sheet.png # Bird sprites (not currently used)
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/                            # Android resources
│   │   └── build.gradle                        # App-level build configuration
│   └── build.gradle                            # Project-level build configuration
└── README.md
```

## Asset Attribution

### Audio Files

**⚠️ IMPORTANT**: The background music file is currently a placeholder and **must be replaced before release**.

- **Background Music** (`main_theme.mp3`):
  - **Current**: Pac-Mania - Block Town (Amiga version)
  - **Source**: https://downloads.khinsider.com/game-soundtracks/album/pac-mania-amiga/02_Block%2520Town.mp3
  - **Status**: ⚠️ **Copyrighted** - Placeholder only, do NOT include in release version
  - **Action Required**: Replace with royalty-free music or obtain proper licensing

- **Sound Effect** (`coin_pickup.wav`):
  - **Credit**: gobbe57 on Freesound
  - **Source**: https://freesound.org/people/gobbe57/sounds/794489/
  - **License**: Free to use with attribution
  - **Attribution**: Sound effect "Coin Pickup" by gobbe57 (freesound.org)

## Git Setup

This project is configured for Git with SSH authentication.

### Setting up SSH Keys for GitHub (First Time)

1. **Generate SSH key** (if you don't have one):
   ```bash
   ssh-keygen -t ed25519 -C "your_email@example.com"
   ```

2. **Add SSH key to GitHub**:
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```
   Copy the output and add it to GitHub:
   - Go to GitHub → Settings → SSH and GPG keys → New SSH key
   - Paste your public key

3. **Test connection**:
   ```bash
   ssh -T git@github.com
   ```

### Using the Repository

```bash
# Clone with SSH
git clone git@github.com:jdpmurphy/flappy-bird-android.git

# Or switch existing HTTPS remote to SSH
git remote set-url origin git@github.com:jdpmurphy/flappy-bird-android.git
```

## Known Issues

### Audio Issues
- **Background music placeholder**: Current music file is copyrighted and must be replaced before release
- **Music initialization**: Music uses lazy initialization on first game start to avoid Context issues

### Gameplay
- **Pipe spacing**: Current spacing is tuned for standard phone screens; may need adjustment for tablets
- **Bird collision**: Uses radius-based collision detection; may feel imprecise at gap edges

### Docker Environment
- **USB device access**: Physical device testing requires proper USB device mounting in Docker
  - May require `--privileged` flag and `/dev/bus/usb` mounting
- **X11 forwarding**: Emulator display requires X11 setup on host (already configured in `dev.sh`)
- **Windows/macOS**: USB device passthrough may require additional configuration

### UI
- **Keyboard input**: Initial entry screen for high scores requires tapping to dismiss keyboard
- **Screen orientations**: App is locked to portrait mode; landscape not supported

## Future Improvements / TODO

### High Priority
- [ ] Replace copyrighted background music with royalty-free alternative
- [ ] Add proper app icon and branding
- [ ] Implement smooth difficulty progression (pipe speed increase over time)
- [ ] Add pause functionality during gameplay
- [ ] Improve collision detection precision

### Gameplay Enhancements
- [ ] Add particle effects (bird feathers, collision sparks)
- [ ] Implement multiple bird skins/characters
- [ ] Add power-ups (shield, slow motion, double points)
- [ ] Create different game modes (endless, time trial, obstacles)
- [ ] Add day/night themes with environment changes

### Visual Improvements
- [ ] Use sprite sheet for bird animation (currently using programmatic drawing)
- [ ] Add parallax scrolling background
- [ ] Implement cloud animations
- [ ] Create pipe variation graphics
- [ ] Add game start countdown animation

### Audio
- [ ] Add more sound effects (wing flap, collision, UI clicks)
- [ ] Implement music track variations
- [ ] Add volume sliders (not just on/off toggles)

### Features
- [ ] Online leaderboard integration
- [ ] Achievement system
- [ ] Daily challenges
- [ ] Social sharing (share score to social media)
- [ ] Tutorial/how-to-play screen
- [ ] Statistics tracking (games played, total score, etc.)

### Technical Improvements
- [ ] Migrate to Kotlin
- [ ] Implement proper MVVM architecture
- [ ] Add unit tests for game logic
- [ ] Add UI tests for activities
- [ ] Optimize memory usage and garbage collection
- [ ] Add analytics tracking (Firebase)
- [ ] Implement proper error handling and crash reporting

### Build & Distribution
- [ ] Set up CI/CD pipeline (GitHub Actions)
- [ ] Create release signing configuration
- [ ] Prepare for Google Play Store release
- [ ] Add ProGuard/R8 optimization for release builds
- [ ] Create app screenshots and store listing materials

## Development Notes

### Game Loop
- Runs at 60 FPS using a dedicated game thread
- Physics update happens every frame (16.67ms target)
- Rendering uses Android SurfaceView with double buffering

### Collision Detection
- Bird uses circular collision boundaries (radius-based)
- Pipes use rectangular collision boxes
- Collision checked every frame against all active pipes

### Performance Considerations
- Pipes are removed from memory when off-screen (left side)
- Sound effects use SoundPool for low-latency playback
- Background music uses MediaPlayer with looping

### Settings Persistence
- Stored in SharedPreferences (`FlappyBirdSettings`)
- High scores stored separately with serialization
- Settings checked on each audio playback call

## Contributing

This is a personal learning project. Feel free to fork and experiment!

If you find issues or have suggestions, feel free to open an issue on GitHub.

## Build Information

- **Gradle Version**: 8.x
- **Android Gradle Plugin**: 8.x
- **Compile SDK**: 34
- **Target SDK**: 34
- **Min SDK**: 24
- **Java Version**: 17
- **Build Tools**: 34.0.0

---

**Note**: This project was developed with assistance from Claude Code, an AI-powered development tool.
