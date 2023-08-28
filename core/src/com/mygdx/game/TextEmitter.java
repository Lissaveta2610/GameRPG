package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class TextEmitter {
    private FlyingText[] items;

    public TextEmitter() {
        items = new FlyingText[50]; //50 заготовок для текстовки
        for (int i = 0; i < items.length; i++) {
            items[i] = new FlyingText();
        }
    }

    public void render(SpriteBatch batch, BitmapFont font24) {
        for (int i = 0; i < items.length; i++) {
            //если активен text, то рисуем
            if (items[i].isActive()) {
                font24.draw(batch, items[i].getText(), items[i].getPosition().x, items[i].getPosition().y);
            }
        }
    }

    //генерация Item
    public void setUp(float x, float y, StringBuilder text) {
        for (int i = 0; i < items.length; i++) {
            //находим в массиве Item неактивную подсказку
            if (!items[i].isActive()) {
                items[i].setUp(x, y, text);
                break;
            }
        }
    }


    public void update(float dt) {
        for (int i = 0; i < items.length; i++) {
            //если активен, то рисуем
            if (items[i].isActive()) {
                items[i].update(dt);
            }
        }
    }
}
