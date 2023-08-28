package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.characters.GameCharacter;
import com.mygdx.game.characters.Hero;
import com.mygdx.game.characters.Monster;

import java.util.*;

public class GameScreen {
    private final SpriteBatch batch;
    private BitmapFont font24;
    private Stage stage;
    private Map map;
    private ItemsEmitter itemsEmitter;
    private TextEmitter textEmitter;
    private Hero hero;
//    private Music music; // длинные звуковые эфф
//    private Sound sound; // короткие зв эфф
    private boolean paused;
    private float spawnTimer;
    private List<GameCharacter> allCharacters;//хранит абсолютно всех
    private List<Monster> allMonsters; //хранит только монстров
    private Comparator<GameCharacter> drawOrderComparator;

    public Hero getHero() {
        return hero;
    }

    public Map getMap() {
        return map;
    }

    public TextEmitter getTextEmitter() {
        return textEmitter;
    }

    public List<Monster> getAllMonsters() {
        return allMonsters;
    }

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    public void create() {
        map = new Map();
        allCharacters = new ArrayList<>();
        allMonsters = new ArrayList<>();
        hero = new Hero(this);
        itemsEmitter = new ItemsEmitter();
        textEmitter = new TextEmitter();
        allCharacters.addAll(Arrays.asList(
                hero,
                new Monster(this),
                new Monster(this),
                new Monster(this),
                new Monster(this),
                new Monster(this),
                new Monster(this)
        ));
        for (int i = 0; i < allCharacters.size(); i++) {
            if (allCharacters.get(i) instanceof Monster) {
                allMonsters.add((Monster) allCharacters.get(i));
            }
        }
        font24 = new BitmapFont(Gdx.files.internal("font24.fnt"));
        stage = new Stage();

        Skin skin = new Skin(); //набор пустого шаблона
        skin.add("SimpleButton", new Texture("SimpleButton.png")); //добавляем в скины (в набор)
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle(); //создаем пустой стиль кнопки
        textButtonStyle.up = skin.getDrawable("SimpleButton"); //если нажата, то выглядит по имени из скина
        textButtonStyle.font = font24; //изображение текста у кнопки

        TextButton pauseButton = new TextButton("Pause", textButtonStyle); //сама кнопка с названием "пауза" и со стилем textButtonStyle
        TextButton exitButton = new TextButton("Exit", textButtonStyle); //сама кнопка с названием "пауза" и со стилем textButtonStyle
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                paused = !paused;
            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        //группируем кнопки
        Group menuGroup = new Group();
        menuGroup.addActor(pauseButton);
        menuGroup.addActor(exitButton);
        exitButton.setPosition(150, 0); //координата кнопки выхода. она у нас смещена от паузы на 150 пикс
        menuGroup.setPosition(1000, 690); //координаты кнопок

        stage.addActor(menuGroup);//слой с интрфейсом, на который можно лепить все
        Gdx.input.setInputProcessor(stage);

        drawOrderComparator = new Comparator<GameCharacter>() {
            @Override
            public int compare(GameCharacter o1, GameCharacter o2) {
                return (int) (o2.getPosition().y - o1.getPosition().y);
            }
        };

//        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));//вкидываю название муз файла. файл находится там, где и картинки
//        music.setLooping(true);
//        music.play();
//
//        sound = Gdx.audio.newSound(Gdx.files.internal("boom.mp3"));
//        sound.play();

    }

    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);
        Gdx.gl.glClearColor(0, 0f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        map.render(batch);
        Collections.sort(allCharacters, drawOrderComparator);
        for (int i = 0; i < allCharacters.size(); i++) {
            allCharacters.get(i).render(batch, font24);
        }
        itemsEmitter.render(batch);
        textEmitter.render(batch, font24);
        hero.renderHUD(batch, font24);
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        if (!paused) {
            spawnTimer += dt;
            if (spawnTimer > 3.0f) { //каждые 3 сек будет создоваться новый монстр
                Monster monster = new Monster(this);
                allCharacters.add(monster);
                allMonsters.add(monster);
                spawnTimer = 0.0f;
            }
            for (int i = 0; i < allCharacters.size(); i++) {
                allCharacters.get(i).update(dt);
            }
            for (int i = 0; i < allMonsters.size(); i++) {
                Monster currentsMonster = allMonsters.get(i);
                if (!currentsMonster.isAlive()) {
                    allMonsters.remove(currentsMonster);
                    allCharacters.remove(currentsMonster);
                    itemsEmitter.generateRandomItem(currentsMonster.getPosition().x, currentsMonster.getPosition().y, 5, 0.6f);
                    hero.killMonster(currentsMonster);
                }
            }
            for (int i = 0; i < itemsEmitter.getItems().length; i++) {
                Item item = itemsEmitter.getItems()[i]; //item - это ссылка на item в массиве
                if (item.isActive()) {
                    float dst = hero.getPosition().dst(item.getPosition());//ищем расстояние
                    if (dst < 24.0f) {
                        hero.useItem(item);
                    }
                }
            }
            textEmitter.update(dt);
            itemsEmitter.update(dt);
        }
        stage.act(dt);
    }
}
