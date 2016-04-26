package com.superduckinvaders.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

/**
 * Responsible for loading game assets.
 */
public class Assets {

    /**
     * Title screen animation.
     */
    public static Animation openingCrawl;

    /**
     *  Player texture sets for normal and flying.
     */
    public static TextureSet playerNormal, playerFlying, playerSwimming, playerMelee;
    public static TextureRegion minimapHead;
    public static Texture minimapRadius;

    public static TextureRegion flatCrawl;

    /**
     *  Bad guy texture sets.
     */
    public static TextureSet badGuyNormal, badGuySwimming, rangedBadGuy, rangedBadGuySwimming, bossBadGuy;

    /**
     * Shadow for the boss
     */
    public static Texture bossShadow = loadTexture("textures/boss/boss_shadow.png");

    /**
     *  Texture for Projectile.
     */
    public static TextureRegion projectile;
    
    /**
     *  Textures for Hearts.
     */
    public static TextureRegion heartFull, heartHalf, heartEmpty;

    /**
     *  Textures for stamina.
     */
    public static TextureRegion staminaFull, staminaEmpty;

    /**
     *  Textures for powerup.
     */
    public static TextureRegion powerupFull, powerupEmpty, small_powerupFull, small_powerupEmpty, healthFull, healthEmpty;

    /**
     *  Animation for explosion.
     */
    public static Animation explosionAnimation;

    /**
     *  Tile maps for each round.
     */
    public static TiledMap[] levels;

    /**
     * Path names for each round in the order that they will be played.
     */
    private static final String[] levelPaths = {"FinalMaps/RoadMap.tmx", "FinalMaps/PathsMap.tmx", "FinalMaps/CsLMBmap.tmx", "FinalMaps/RonCookeMap.tmx",
            "FinalMaps/RoadMap.tmx", "FinalMaps/CsLMBmap.tmx", "FinalMaps/RonCookeMap.tmx", "FinalMaps/PathsMapBoss.tmx"};

    /**
     * Number of mobs to spawn in level.
     */
    public static final int[] levelMobs = {20, 20, 20, 20, 20, 20, 20, 20};

    /**
     *  The font for the UI.
     */
    public static BitmapFont font;

    /**
     * The texture for the button.
     */
    public static TextureRegion button, button_hover;

    /**
     *  Textures for floor items.
     */
    public static TextureRegion floorItemGun, floorItemSpeed, floorItemInvulnerable, floorItemScore, floorItemFireRate;

    /**
     *  Texture for objective flag.
     */
    public static TextureRegion flag;

    /**
     *  Texture for the game logo.
     */
    public static TextureRegion logo;

    /**
     * Responsible for loading maps.
     */
    private static TmxMapLoader mapLoader = new TmxMapLoader();

    /**
     * Custom cursor to be used.
     */
    public static Pixmap cursor;

    /**
     * Shadow for mobs.
     */
    public static Texture mobShadow;

    /**
     * Player mobShadow.
     */
    public static Texture playerShadow;

    /**
     * 'ROUND' text for beginning of round.
     */
    public static Texture roundText;

    /**
     * Number to be used at the beginning of round.
     */
    public static Texture[] roundNums;

    /**
     * Background
     */
    public static Texture bg;

    /**
     * Sound effects
     */
    public static Sound laser, saber, saberHit, flying, pickup;

    /**
     * Music
     */
    public static Music title, main;


    /**
     * Loads all assets.
     */
    public static void load() {
        loadPlayerTextureSets();
        loadBadGuyTextureSet();
        loadFloorItems();

        laser = Gdx.audio.newSound(Gdx.files.internal("sfx/trprsht1.wav"));
        saber = Gdx.audio.newSound(Gdx.files.internal("sfx/Swing02.wav"));
        saberHit = Gdx.audio.newSound(Gdx.files.internal("sfx/lasrhit3.wav"));
        flying = Gdx.audio.newSound(Gdx.files.internal("sfx/hover.wav"));
        pickup = Gdx.audio.newSound(Gdx.files.internal("sfx/pickup.wav"));

        //https://www.youtube.com/watch?v=YjisU0YmKN0&list=PLu_f2AnvQFcAfyROp0mznY8yJxGWkIutq&index=1 link
        title = Gdx.audio.newMusic(Gdx.files.internal("sfx/TitleTheme.ogg"));
        title.setLooping(true);

        //https://www.youtube.com/watch?v=qO5xLNW7q4E link
        main = Gdx.audio.newMusic(Gdx.files.internal("sfx/Main.ogg"));
        main.setLooping(true);

        bg = new Texture("textures/bg_starfield_xl.jpg");

        projectile = new TextureRegion(loadTexture("textures/projectile2.png"));

        explosionAnimation = loadAnimation("textures/explosion.png", 2, 16, 0.15f);

        openingCrawl = loadAnimation("textures/OpeningCrawl.png",16,16,512,288,0.13f);

        //Load the maps for the levels
        levels = new TiledMap[8];
        for (int i = 0; i < 8; i++) loadLevel(i);

        font = loadFont("font/gamefont2.fnt");

        Texture hearts = loadTexture("textures/hearts.png");
        heartFull = new TextureRegion(hearts, 0, 0, 32, 28);
        heartHalf = new TextureRegion(hearts, 32, 0, 32, 28);
        heartEmpty = new TextureRegion(hearts, 64, 0, 32, 28);

        Texture stamina = loadTexture("textures/stamina.png");
        staminaFull = new TextureRegion(stamina, 0, 0, 192, 28);
        staminaEmpty = new TextureRegion(stamina, 0, 28, 192, 28);

        Texture powerup = loadTexture("textures/powerup.png");
        powerupFull = new TextureRegion(powerup, 0, 0, 192, 28);
        powerupEmpty = new TextureRegion(powerup, 0, 28, 192, 28);

        Texture small_powerup = loadTexture("textures/small_powerup.png");
        small_powerupFull = new TextureRegion(small_powerup, 0, 0, 96, 14);
        small_powerupEmpty = new TextureRegion(small_powerup, 0, 14, 96, 14);

        Texture health = loadTexture("textures/health_bar.png");
        healthFull = new TextureRegion(health, 0, 0, 100, 14);
        healthEmpty = new TextureRegion(health, 0, 14, 100, 14);


        button = new TextureRegion(loadTexture("textures/button.png"));
        button_hover = new TextureRegion(loadTexture("textures/button_pressed.png"));

        flag = new TextureRegion(loadTexture("textures/flag.png"));
        logo = new TextureRegion(loadTexture("textures/logo.png"));

        cursor = new Pixmap(Gdx.files.internal("textures/cursor_crosshair.png"));

        mobShadow = new Texture("textures/mobShadow.png");
        playerShadow = new Texture("textures/playerShadow.png");

        roundText = new Texture("RoundFonts/Round.png");

        roundNums = new Texture[10];
        for (int x=0;x<10;x++) {
            roundNums[x] = new Texture("RoundFonts/"+x+".png");
        }

        //flatCrawl = new TextureRegion(loadTexture("textures/flat_crawl.png"));
    }

    /**
     * Loads assets relating to the player in the normal state.
     * If you change the player texture size, be sure to change the values here.
     */
    private static void loadPlayerTextureSets() {

        minimapHead = new TextureRegion(loadTexture("textures/minimap_head.png"));
        minimapRadius = loadTexture("textures/minimap_radius.png");
        // Load idle texture map.
        Texture playerIdle = loadTexture("textures/player_walking/player_idle.png");

        // Cut idle textures from texture map.
        TextureRegion front = new TextureRegion(playerIdle, 0, 0, 32, 64);
        TextureRegion frontLeft = new TextureRegion(playerIdle, 32, 0, 32, 64);
        TextureRegion left = new TextureRegion(playerIdle, 64, 0, 32, 64);
        TextureRegion backLeft = new TextureRegion(playerIdle, 96, 0, 32, 64);
        TextureRegion back = new TextureRegion(playerIdle, 128, 0, 32, 64);
        TextureRegion backRight = new TextureRegion(playerIdle, 160, 0, 32, 64);
        TextureRegion right = new TextureRegion(playerIdle, 192, 0, 32, 64);
        TextureRegion frontRight = new TextureRegion(playerIdle, 224, 0, 32, 64);

        // Load idle swimming texture map.
        Texture playerIdleSwim = loadTexture("textures/player_swimming/player_idle_swimming.png");

        // Cut idle swimming textures from texture map.
        TextureRegion frontSwim = new TextureRegion(playerIdleSwim, 0, 0, 32, 64);
        TextureRegion frontLeftSwim = new TextureRegion(playerIdleSwim, 32, 0, 32, 64);
        TextureRegion leftSwim = new TextureRegion(playerIdleSwim, 64, 0, 32, 64);
        TextureRegion backLeftSwim = new TextureRegion(playerIdleSwim, 96, 0, 32, 64);
        TextureRegion backSwim = new TextureRegion(playerIdleSwim, 128, 0, 32, 64);
        TextureRegion backRightSwim = new TextureRegion(playerIdleSwim, 160, 0, 32, 64);
        TextureRegion rightSwim = new TextureRegion(playerIdleSwim, 192, 0, 32, 64);
        TextureRegion frontRightSwim = new TextureRegion(playerIdleSwim, 224, 0, 32, 64);


        // Load walking animations.
        Animation walkingFront = loadAnimation("textures/player_walking/player_walking_front.png", 4, 32, 0.2f);
        Animation walkingFrontLeft = loadAnimation("textures/player_walking/player_walking_front_left.png", 4, 32, 0.2f);
        Animation walkingLeft = loadAnimation("textures/player_walking/player_walking_left.png", 4, 32, 0.2f);
        Animation walkingBackLeft = loadAnimation("textures/player_walking/player_walking_back_left.png", 4, 32, 0.2f);
        Animation walkingBack = loadAnimation("textures/player_walking/player_walking_back.png", 4, 32, 0.2f);
        Animation walkingBackRight = loadAnimation("textures/player_walking/player_walking_back_right.png", 4, 32, 0.2f);
        Animation walkingRight = loadAnimation("textures/player_walking/player_walking_right.png", 4, 32, 0.2f);
        Animation walkingFrontRight = loadAnimation("textures/player_walking/player_walking_front_right.png", 4, 32, 0.2f);


        // Load flying textures
        Texture flying = loadTexture("textures/player_flying/player_flying.png");
        TextureRegion flyingRight = new TextureRegion(flying, 0, 0, 64, 64);
        TextureRegion flyingLeft = new TextureRegion(flying, 64, 0, 64, 64);


        //Load swimming animations
        Animation swimmingFront = loadAnimation("textures/player_swimming/player_swimming_front.png", 4, 32, 0.2f);
        Animation swimmingFrontLeft = loadAnimation("textures/player_swimming/player_swimming_front_left.png", 4, 32, 0.2f);
        Animation swimmingLeft = loadAnimation("textures/player_swimming/player_swimming_left.png", 4, 32, 0.2f);
        Animation swimmingBackLeft = loadAnimation("textures/player_swimming/player_swimming_back_left.png", 4, 32, 0.2f);
        Animation swimmingBack = loadAnimation("textures/player_swimming/player_swimming_back.png", 4, 32, 0.2f);
        Animation swimmingBackRight = loadAnimation("textures/player_swimming/player_swimming_back_right.png", 4, 32, 0.2f);
        Animation swimmingRight = loadAnimation("textures/player_swimming/player_swimming_right.png", 4, 32, 0.2f);
        Animation swimmingFrontRight = loadAnimation("textures/player_swimming/player_swimming_front_right.png", 4, 32, 0.2f);

        //Load Melee animations
        Animation meleeFront = loadAnimation("textures/player_melee/player_melee_front.png", 4, 64, 0.1f);
        Animation meleeFrontLeft = loadAnimation("textures/player_melee/player_melee_front_left.png", 4, 64, 0.1f);
        Animation meleeLeft = loadAnimation("textures/player_melee/player_melee_left.png", 4, 64, 0.1f);
        Animation meleeBackLeft = loadAnimation("textures/player_melee/player_melee_back_left.png", 4, 64, 0.1f);
        Animation meleeBack = loadAnimation("textures/player_melee/player_melee_back.png", 4, 64, 0.1f);
        Animation meleeBackRight = loadAnimation("textures/player_melee/player_melee_back_right.png", 4, 64, 0.1f);
        Animation meleeRight = loadAnimation("textures/player_melee/player_melee_right.png", 4, 64, 0.1f);
        Animation meleeFrontRight = loadAnimation("textures/player_melee/player_melee_front_right.png", 4, 64, 0.1f);

        //Creates texture sets for each movement type.
        playerNormal = new TextureSet(front, frontLeft, left, backLeft, back, backRight, right, frontRight,
                walkingFront, walkingFrontLeft, walkingLeft, walkingBackLeft, walkingBack, walkingBackRight, walkingRight, walkingFrontRight);

        playerFlying = new TextureSet(front, frontLeft, flyingLeft, backLeft, back, backRight, flyingRight, frontRight,
                walkingFront, walkingFrontLeft, walkingLeft, walkingBackLeft, walkingBack, walkingBackRight, walkingRight, walkingFrontRight);

        playerSwimming = new TextureSet(frontSwim, frontLeftSwim, leftSwim, backLeftSwim, backSwim, backRightSwim, rightSwim, frontRightSwim,
                swimmingFront, swimmingFrontLeft, swimmingLeft, swimmingBackLeft, swimmingBack, swimmingBackRight, swimmingRight, swimmingFrontRight);

        playerMelee = new TextureSet(front, frontLeft, left, backLeft, back, backRight, right, frontRight,
                meleeFront, meleeFrontLeft, meleeLeft, meleeBackLeft, meleeBack, meleeBackRight, meleeRight, meleeFrontRight);
    }

    /**
     * Loads the textures from the bad guy textures file.
     */
    private static void loadBadGuyTextureSet() {
        // Melee Enemy
        Texture badGuyIdle = loadTexture("textures/stormtrooper_enemy/badguy_idle.png");

        // Cut idle textures from texture map.
        TextureRegion front = new TextureRegion(badGuyIdle, 0, 0, 21, 24);
        TextureRegion back = new TextureRegion(badGuyIdle, 21, 0, 21, 24);
        TextureRegion left = new TextureRegion(badGuyIdle, 42, 0, 21, 24);
        TextureRegion right = new TextureRegion(badGuyIdle, 63, 0, 21, 24);

        // Load walking animations.
        Animation walkingFront = loadAnimation("textures/stormtrooper_enemy/badguy_walking_front.png", 4, 21, 0.2f);
        Animation walkingBack = loadAnimation("textures/stormtrooper_enemy/badguy_walking_back.png", 4, 21, 0.2f);
        Animation walkingLeft = loadAnimation("textures/stormtrooper_enemy/badguy_walking_left.png", 4, 21, 0.2f);
        Animation walkingRight = loadAnimation("textures/stormtrooper_enemy/badguy_walking_right.png", 4, 21, 0.2f);

        badGuyNormal = new TextureSet(front, back, left, right, walkingFront, walkingBack, walkingLeft, walkingRight);

        //Melee Swimming
        Texture badGuyIdleSwim = loadTexture("textures/stormtrooper_enemy/badguy_idle_swimming.png");

        // Cut idle textures from texture map.
        TextureRegion frontSwim = new TextureRegion(badGuyIdleSwim, 0, 0, 32, 22);
        TextureRegion backSwim = new TextureRegion(badGuyIdleSwim, 32, 0, 32, 22);
        TextureRegion leftSwim = new TextureRegion(badGuyIdleSwim, 64, 0, 32, 22);
        TextureRegion rightSwim = new TextureRegion(badGuyIdleSwim, 96, 0, 32, 22);

        // Load walking animations.
        Animation walkingFrontSwim = loadAnimation("textures/stormtrooper_enemy/badguy_walking_front_swimming.png", 4, 21, 0.2f);
        Animation walkingBackSwim = loadAnimation("textures/stormtrooper_enemy/badguy_walking_back_swimming.png", 4, 21, 0.2f);
        Animation walkingLeftSwim = loadAnimation("textures/stormtrooper_enemy/badguy_walking_left_swimming.png", 4, 21, 0.2f);
        Animation walkingRightSwim = loadAnimation("textures/stormtrooper_enemy/badguy_walking_right_swimming.png", 4, 21, 0.2f);

        badGuySwimming = new TextureSet(frontSwim, backSwim, leftSwim, rightSwim, walkingFrontSwim, walkingBackSwim, walkingLeftSwim, walkingRightSwim);


        // Ranged enemy
        Texture rangedBadGuyIdle = loadTexture("textures/squirrel_enemy/badguy_idle.png");

        // Cut idle textures from texture map.
        TextureRegion rangedfront = new TextureRegion(rangedBadGuyIdle, 0, 0, 32, 64);
        TextureRegion rangedback = new TextureRegion(rangedBadGuyIdle, 32, 0, 32, 64);
        TextureRegion rangedleft = new TextureRegion(rangedBadGuyIdle, 64, 0, 32, 64);
        TextureRegion rangedright = new TextureRegion(rangedBadGuyIdle, 96, 0, 32, 64);

        // Load walking animations.
        Animation rangedWalkingFront = loadAnimation("textures/squirrel_enemy/badguy_walking_front.png", 4, 32, 0.2f);
        Animation rangedWalkingBack = loadAnimation("textures/squirrel_enemy/badguy_walking_back.png", 4, 32, 0.2f);
        Animation rangedWalkingLeft = loadAnimation("textures/squirrel_enemy/badguy_walking_left.png", 4, 32, 0.2f);
        Animation rangedWalkingRight = loadAnimation("textures/squirrel_enemy/badguy_walking_right.png", 4, 32, 0.2f);

        rangedBadGuy = new TextureSet(rangedfront, rangedback, rangedleft, rangedright, rangedWalkingFront, rangedWalkingBack, rangedWalkingLeft, rangedWalkingRight);

        //Ranged Swimming
        Texture rangedBadGuyIdleSwim = loadTexture("textures/squirrel_enemy/badguy_idle_swim.png");

        // Cut idle textures from texture map.
        TextureRegion rangedfrontSwim = new TextureRegion(rangedBadGuyIdleSwim, 0, 0, 21, 24);
        TextureRegion rangedbackSwim = new TextureRegion(rangedBadGuyIdleSwim, 21, 0, 21, 24);
        TextureRegion rangedleftSwim = new TextureRegion(rangedBadGuyIdleSwim, 42, 0, 21, 24);
        TextureRegion rangedrightSwim = new TextureRegion(rangedBadGuyIdleSwim, 63, 0, 21, 24);

        // Load walking animations.
        Animation rangedWalkingFrontSwim = loadAnimation("textures/squirrel_enemy/badguy_walking_front_swim.png", 4, 32, 0.2f);
        Animation rangedWalkingBackSwim = loadAnimation("textures/squirrel_enemy/badguy_walking_back_swim.png", 4, 32, 0.2f);
        Animation rangedWalkingLeftSwim = loadAnimation("textures/squirrel_enemy/badguy_walking_left_swim.png", 4, 32, 0.2f);
        Animation rangedWalkingRightSwim = loadAnimation("textures/squirrel_enemy/badguy_walking_right_swim.png", 4, 32, 0.2f);

        rangedBadGuySwimming = new TextureSet(rangedfrontSwim, rangedbackSwim, rangedleftSwim, rangedrightSwim, rangedWalkingFrontSwim, rangedWalkingBackSwim, rangedWalkingLeftSwim, rangedWalkingRightSwim);

        Texture boss = loadTexture("textures/boss/boss.png");

        TextureRegion boss2 = new TextureRegion(boss,0,0,60,69);
        Animation boss3 = loadAnimation("textures/boss/boss.png", 1, 60, 0);

        bossBadGuy = new TextureSet(boss2,boss2,boss2,boss2,boss3,boss3,boss3,boss3);

    }

    /**
     * Loads the texture from the floor items file.
     */
    public static void loadFloorItems() {
        Texture floorItems = loadTexture("textures/floor_items.png");

        floorItemGun = new TextureRegion(floorItems, 0, 0, 15, 15);
        floorItemSpeed = new TextureRegion(floorItems, 15, 0, 15, 15);
        floorItemInvulnerable = new TextureRegion(floorItems, 30, 0, 15, 15);
        floorItemScore = new TextureRegion(floorItems, 45, 0, 15, 15);
        floorItemFireRate = new TextureRegion(floorItems, 60, 0, 15, 15);
    }

    /**
     * Loads the texture from the specified file.
     *
     * @param file the file to load from
     * @return the texture
     */
    public static Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    /**
     * Loads the tile map for a particular level.
     *
     * @param i the level to be loaded from file.
     */
    public static void loadLevel(int i) {
        levels[i] = mapLoader.load(levelPaths[i]);
    }

    /**
     * Loads the animation from the specified file.
     *
     * @param file          the file to load from
     * @param count         how many frames are in the file
     * @param frameWidth    how wide each frame is in the file
     * @param frameDuration how long each frame should be shown for in seconds
     * @return the animation
     */
    public static Animation loadAnimation(String file, int count, int frameWidth, float frameDuration) {
        Texture texture = loadTexture(file);
        Array<TextureRegion> keyFrames = new Array<TextureRegion>();

        for (int i = 0; i < count; i++) {
            keyFrames.add(new TextureRegion(texture, i * frameWidth, 0, frameWidth, texture.getHeight()));
        }

        return new Animation(frameDuration, keyFrames);
    }

    public static Animation loadAnimation(String file, int rowCount, int columnCount, int frameWidth, int frameHeight, float frameDuration) {
        TextureRegion[][] textures = TextureRegion.split(loadTexture(file),frameWidth,frameHeight);

        TextureRegion[] keyFrames = new TextureRegion[rowCount*columnCount];

        int index = 0;
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                keyFrames[index]=textures[row][col];
                index++;
            }
        }

        return new Animation(frameDuration, keyFrames);

    }

    /**
     * Loads the bitmap font from the specified files.
     *
     * @param fontFile  the file containing information about the glyphs stored on the image file
     * @return the bitmap font
     */
    public static BitmapFont loadFont(String fontFile) {
        return new BitmapFont(Gdx.files.internal(fontFile));
    }


}
