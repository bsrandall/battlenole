package com.android.battlenoleproject;

/**
 * Created by stephanie on 7/11/15.
 * This class contains the title and text displayed on the help and credit pages.
 * More pages can be added by simply adding data to the mPages array.
 */
public class Info {
    private Page[] mPages;

    public Info() {
        mPages = new Page[2];

        mPages[0] = new Page(
                "Rules of Engagement",
                "1. When the game launches, you can either send another player an invitation to " +
                        "play or accept one that’s been sent to you.\n" +
                        "2. Start by positioning your ships.  This is accomplished randomly. " +
                        "You can continue to redeploy your ships until they’re placed where you " +
                        "want them.  Lock them in and begin play.\n" +
                        "3. Each player starts out with 5 torpedoes, equivalent to the number of " +
                        "ships s/he has.\n" +
                        "4. Each takes a turn firing, hoping to hit his/her opponent’s ships.  " +
                        "As your ships are sunk, you lose torpedoes.  First player to sink all " +
                        "of his/her opponent’s ships wins!");

        mPages[1] = new Page(
                "Team Froyo",
                "Brian Randall\nJulian Loreti\nStephanie Brown\nWilliam Henry");
    }

    public Page getPage(int pageNumber) {
        return mPages[pageNumber];
    }

}
