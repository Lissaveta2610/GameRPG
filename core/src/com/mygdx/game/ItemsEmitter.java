package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class ItemsEmitter {
    private Item[] items;
    private Texture texture;
    private TextureRegion[] regions;

    public Item[] getItems() {
        return items;
    }

    public ItemsEmitter() {
        items = new Item[50]; //ограничение для Item
        for (int i = 0; i < items.length; i++) {
            items[i] = new Item();
        }
        texture = new Texture("Items.png");
        regions = new TextureRegion(texture).split(32, 32)[0];//бьем текстуру монет на регионы и взяли первую строку [0] из этого набора
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < items.length; i++) {
            //если активен, то рисуем
            if (items[i].isActive()) {
                batch.draw(regions[items[i].getType().index], items[i].getPosition().x - 16, items[i].getPosition().y - 16); //16 - половина размера монетки
            }
        }
    }

    //генерация Item
    public void generateRandomItem(float x, float y, int count, float probability) {
        for (int q = 0; q < count; q++) {
            float num = MathUtils.random(0.0f, 1.0f);
            if (num <= probability) {
                int type = MathUtils.random(0, Item.Type.values().length - 1);
                for (int i = 0; i < items.length; i++) {
                    //находим в массиве Item неактивный
                    if (!items[i].isActive()) {
                        items[i].setUp(x, y, Item.Type.values()[type]);
                        break;
                    }
                }
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
