package edu.msu.harr1332.project1.Cloud;
/*
import edu.msu.harr1332.project1.Cloud.Models.Catalog;
import edu.msu.cse476.cloudhatter.Cloud.Models.LoadResult;*/
import edu.msu.harr1332.project1.Cloud.Models.Catalog;
import edu.msu.harr1332.project1.Cloud.Models.CreateResult;
import edu.msu.harr1332.project1.Cloud.Models.DeleteResult;
import edu.msu.harr1332.project1.Cloud.Models.GetGameResult;
import edu.msu.harr1332.project1.Cloud.Models.GetResult;
import edu.msu.harr1332.project1.Cloud.Models.JoinResult;
import edu.msu.harr1332.project1.Cloud.Models.LoginResult;
import edu.msu.harr1332.project1.Cloud.Models.GameResult;


import edu.msu.harr1332.project1.Cloud.Models.RecieveResult;
import edu.msu.harr1332.project1.Cloud.Models.UpdateResult;
import edu.msu.harr1332.project1.Pipe;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


import static edu.msu.harr1332.project1.Cloud.Cloud.DELETE_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.GET_GAME_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.JOIN_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.CREATE_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.GAME_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.CATALOG_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.GET_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.RECIEVE_PATH;
import static edu.msu.harr1332.project1.Cloud.Cloud.UPDATE_PATH;


/*
import static edu.msu.cse476.cloudhatter.Cloud.Cloud.DELETE_PATH;
import static edu.msu.cse476.cloudhatter.Cloud.Cloud.LOAD_PATH;
import static edu.msu.cse476.cloudhatter.Cloud.Cloud.SAVE_PATH;*/

public interface SteampunkedService {

    @GET(LOGIN_PATH)
    Call<LoginResult> login(
            @Query("user") String userId,
            @Query("pw") String password
    );

    @GET(GET_PATH)
    Call<GetResult> loadGame(
            @Query("id") int id
    );

    @GET(GET_GAME_PATH)
    Call<GetGameResult> getGame(
            @Query("id") int id
    );

    @GET(CATALOG_PATH)
    Call<Catalog> getCatalog(
            @Query("test") String test
    );

    @FormUrlEncoded
    @POST(GAME_PATH)
    Call<GameResult> updateGame(
            @Field("player1") String player1,
            @Field("player2") String player2,
            @Field("pipes") String pipes,
            @Field("randomPipes") String randomPipes,
            @Field("turn") int turn,
            @Field("gameSize") int gameSize
    );

    @FormUrlEncoded
    @POST(DELETE_PATH)
    Call<DeleteResult> delete(
            @Field("id") int id
    );



    @FormUrlEncoded
    @POST(UPDATE_PATH)
    Call<UpdateResult> updateValues(
            @Field("id") int id,
            @Field("player1") String player1,
            @Field("player2") String player2,
            @Field("pipes") String pipes,
            @Field("randomPipes") String randomPipes,
            @Field("turn") int turn
    );

    @GET(RECIEVE_PATH)
    Call<RecieveResult> recieveValues(
            @Field("id") int id,
            @Field("player1") String player1,
            @Field("player2") String player2,
            @Field("pipes") String pipes,
            @Field("randomPipes") String randomPipes,
            @Field("turn") String turn
    );

    @FormUrlEncoded
    @POST(JOIN_PATH)
    Call<JoinResult> joinGame(
            @Field("id") int id,
            @Field("playerName") String playerName
    );

    @FormUrlEncoded
    @POST(CREATE_PATH)
    Call<CreateResult> createAccount(
            @Field("user") String userId,
            @Field("pw") String password
    );
    /*
    @GET(CATALOG_PATH)
    Call<Catalog> getCatalog(
            @Query("user") String userId,
            @Query("magic") String magic,
            @Query("pw") String password
    );

    @GET(LOAD_PATH)
    Call<LoadResult> loadHat(
            @Query("user") String userId,
            @Query("magic") String magic,
            @Query("pw") String password,
            @Query("id") String idHatToLoad
    );

    @GET(DELETE_PATH)
    Call<LoadResult> deleteHat(
            @Query("user") String userId,
            @Query("magic") String magic,
            @Query("pw") String password,
            @Query("id") String idHatToLoad
    );

    @FormUrlEncoded
    @POST(SAVE_PATH)
    Call<SaveResult> saveHat(@Field("xml") String xmlData);*/
}
