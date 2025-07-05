package de.aropix.mcs2mqtt.hooks;

import java.util.List;

import de.aropix.mcs2mqtt.MqttHandler;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class GetRecipeHook {
    public static void initHook(ClassLoader classLoader, MqttHandler mqtt) throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(
                "com.tecpal.device.fragments.guidecook.presenter.GuidedCookRecipePresenter$b",
                classLoader,
                "a", // ProGuarded method name = set_recipe
                Class.forName("com.tgi.library.device.database.entity.RecyclerRecipeEntity", false, classLoader),
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object recipe = param.args[0];
                        if (recipe == null) {
                            XposedBridge.log("set_recipe was called with null");
                            return;
                        }

                        Long id = (Long) XposedHelpers.callMethod(recipe, "getId");
                        Long translationId = (Long) XposedHelpers.callMethod(recipe, "getTranslationId");
                        String language = (String) XposedHelpers.callMethod(recipe, "getLanguage");
                        //AuthorEntity author = (AuthorEntity) XposedHelpers.callMethod(recipe, "getAuthor");
                        String name = (String) XposedHelpers.callMethod(recipe, "getName");
                        String status = (String) XposedHelpers.callMethod(recipe, "getStatus");
                        String lastUpdated = (String) XposedHelpers.callMethod(recipe, "getLastUpdated");
                        String createdDate = (String) XposedHelpers.callMethod(recipe, "getCreatedDate");
                        String publishedDate = (String) XposedHelpers.callMethod(recipe, "getPublishedDate");
                        String description = (String) XposedHelpers.callMethod(recipe, "getDescription");
                        //ImageEntity thumbnail = (ImageEntity) XposedHelpers.callMethod(recipe, "getThumbnail");
                        //ImageEntity detailsImage = (ImageEntity) XposedHelpers.callMethod(recipe, "getDetailsImage");
                        String complexity = (String) XposedHelpers.callMethod(recipe, "getComplexity");
                        Integer complexityLevel = (Integer) XposedHelpers.callMethod(recipe, "getComplexityLevel");
                        String popularity = (String) XposedHelpers.callMethod(recipe, "getPopularity");
                        //String rating = (String) XposedHelpers.callMethod(recipe, "getRating");
                        Integer totalRating = (Integer) XposedHelpers.callMethod(recipe, "getTotalRating");
                        Long defaultServingSizeId = (Long) XposedHelpers.callMethod(recipe, "getDefaultServingSizeId");
                        Long currentServingSizeId = (Long) XposedHelpers.callMethod(recipe, "getCurrentServingSizeId");
                        String source = (String) XposedHelpers.callMethod(recipe, "getSource");
                        String url = (String) XposedHelpers.callMethod(recipe, "getUrl");
                        String media = (String) XposedHelpers.callMethod(recipe, "getMedia");
                        String mediaArchiveUrl = (String) XposedHelpers.callMethod(recipe, "getMediaArchiveUrl");
                        String mediaArchiveMd5 = (String) XposedHelpers.callMethod(recipe, "getMediaArchiveMd5");
                        Integer downloaded = (Integer) XposedHelpers.callMethod(recipe, "getDownloaded");
                        Integer duration = (Integer) XposedHelpers.callMethod(recipe, "getDuration");
                        int progress = (int) XposedHelpers.callMethod(recipe, "getProgress");
                        //List<ServingSizeEntity> servingSizes = (List<ServingSizeEntity>) XposedHelpers.callMethod(recipe, "getServingSizes");
                        //List<StepEntity> currentStepList = (List<StepEntity>) XposedHelpers.callMethod(recipe, "getCurrentStepList");


                        XposedBridge.log("---- Recipe Data ----");
                        XposedBridge.log("ID: " + id);
                        XposedBridge.log("Translation ID: " + translationId);
                        XposedBridge.log("Language: " + language);
                        //XposedBridge.log("Author: " + author);
                        XposedBridge.log("Name: " + name);
                        XposedBridge.log("Status: " + status);
                        XposedBridge.log("Last Updated: " + lastUpdated);
                        XposedBridge.log("Created Date: " + createdDate);
                        XposedBridge.log("Published Date: " + publishedDate);
                        XposedBridge.log("Description: " + description);
                        //XposedBridge.log("Thumbnail: " + thumbnail);
                        //XposedBridge.log("Details Image: " + detailsImage);
                        XposedBridge.log("Complexity: " + complexity);
                        XposedBridge.log("Complexity Level: " + complexityLevel);
                        XposedBridge.log("Popularity: " + popularity);
                        //XposedBridge.log("Rating: " + rating);
                        XposedBridge.log("Total Rating: " + totalRating);
                        XposedBridge.log("Default Serving Size ID: " + defaultServingSizeId);
                        XposedBridge.log("Current Serving Size ID: " + currentServingSizeId);
                        XposedBridge.log("Source: " + source);
                        XposedBridge.log("URL: " + url);
                        XposedBridge.log("Media: " + media);
                        XposedBridge.log("Media Archive URL: " + mediaArchiveUrl);
                        XposedBridge.log("Media Archive MD5: " + mediaArchiveMd5);
                        //XposedBridge.log("Serving Sizes: " + servingSizes);
                        //XposedBridge.log("Current Step List: " + currentStepList);
                        XposedBridge.log("Downloaded: " + downloaded);
                        XposedBridge.log("Duration: " + duration);
                        XposedBridge.log("Progress: " + progress);
                        XposedBridge.log("---------------------");

                        String payload = "{"
                                + "\"recipe_name\": \"" + name + "\""
                                //+ "\"weight\": " + data.getWeight() + ", "
                                //+ "\"time\": " + time + ", "
                                //+ "\"speed\": " + data.getSpeed() + ", "
                                //+ "\"running\": " + data.getIdleness()
                                + "}";

                        mqtt.sendPayload(payload, "mcs/recipe");


                    }
                }
        );
    }
    }
