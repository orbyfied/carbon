package com.github.orbyfied.carbon.crafting;

/**
 * The result generator which is
 * supposed to fill the result slot
 * based on the chosen recipe. If
 * no result generator is provided
 * the recipe will just be returned
 * and won't be processed further.
 *
 * This can be used to select recipes
 * in blocks like furnaces which don't
 * require an instant result supply.
 */
public interface Result {

    void write(
            /* TODO */
    );

}
