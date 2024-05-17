package com.example.androidexample.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Admin.AdminActivity;
import com.example.androidexample.Login.LoginPageActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.Networking.MultipartRequest;
import com.example.androidexample.R;
import com.example.androidexample.Networking.VolleySingleton2;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserProfileActivity extends AppCompatActivity {

    private UserObject user;

    private ImageButton profilePic;
    private EditText userNameText;
    private Button back, deleteImage, usernameChange, passwordChange, deleteUser, showAllUsers, adminCreate, admin, logout;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpic);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        profilePic = findViewById(R.id.setUserProfilePic);
        back = findViewById(R.id.backToMain);
        deleteImage = findViewById(R.id.deleteImage);
        queue = Volley.newRequestQueue(this);
        usernameChange = findViewById(R.id.changeUserButton);
        passwordChange = findViewById(R.id.changePassButton);
        deleteUser = findViewById(R.id.deleteUserButton);
        showAllUsers = findViewById(R.id.showUsers);
        admin = findViewById(R.id.adminButton);
        adminCreate = findViewById(R.id.makeAdminButton);
        logout = findViewById(R.id.logout);
        userNameText = findViewById(R.id.profileUserName);
        userNameText.setText(user.getUserName());


        makeImageRequest();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(UserProfileActivity.this).crop().compress(1024).maxResultSize(1080, 1080).start();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, MainActivity.httpServerAddress + "/image/delete/" + user.getId(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);
                user.setProfilePicture(null);
                profilePic.setImageResource(R.drawable.profilepicturesample);
            }
        });

        passwordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, PasswordChangeActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        usernameChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserProfileActivity.this, UsernameChangeActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = MainActivity.httpServerAddress + "/users/deletebyid/" + user.getId();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent = new Intent(UserProfileActivity.this, LoginPageActivity.class);
                        Toast.makeText(getApplicationContext(), "User deleted", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
                queue.add(request);
            }
        });

        showAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ShowAllUsersActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admin.setVisibility(View.VISIBLE);
                user.makeAdmin();
                RequestQueue queue = Volley.newRequestQueue(UserProfileActivity.this);
                JSONObject body = new JSONObject();
                try {
                    body.put("id", user.getId());
                    body.put("password", user.getPassword());
                    body.put("name", user.getUserName());
                } catch (Exception e) {

                }
                String url = MainActivity.httpServerAddress + "/admin/createAdmin";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, AdminActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
        admin.setVisibility(View.INVISIBLE);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });

        String newUrl = MainActivity.httpServerAddress + "/admin/getAllAdmin";
        JsonArrayRequest request1 = new JsonArrayRequest(Request.Method.GET, newUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (user.getUserName().equals(object.getString("name"))) {
                            user.makeAdmin();
                            break;
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (user.isAdmin()) {
                    admin.setVisibility(View.VISIBLE);
                } else {
                    admin.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(request1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        profilePic.setImageURI(uri);
        String base64EncodedImage = ImageUtils.encodeImageToBase64(getContentResolver(), uri);
        user.setProfilePicture(base64EncodedImage);
        byte[] imageData = convertImageUriToBytes(uri);
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, MainActivity.httpServerAddress + "/image/post/" + user.getId(), imageData, response -> {
            // Handle response
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            Log.d("Upload", "Response: " + response);
        }, error -> {
            // Handle error
//            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("Upload", "Error: " + error.getMessage());
        });

        VolleySingleton2.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);
    }

    private byte[] convertImageUriToBytes(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void makeImageRequest() {

        ImageRequest imageRequest = new ImageRequest(
                MainActivity.httpServerAddress + "/image/getbyid/"+user.getId(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
                        profilePic.setImageBitmap(response);
                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton2.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }
}
