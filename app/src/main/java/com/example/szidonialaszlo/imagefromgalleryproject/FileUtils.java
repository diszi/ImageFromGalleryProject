package com.example.szidonialaszlo.imagefromgalleryproject;


import android.content.ContentUris;
        import android.content.Context;

        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Build;
        import android.os.Environment;
        import android.provider.DocumentsContract;
        import android.provider.MediaStore;
        import android.webkit.MimeTypeMap;

        import java.io.File;


/**
 * Created by szidonia.laszlo on 2017. 11. 07..
 */

public class FileUtils {



    private FileUtils(){}

    public static final String MIME_TYPE_IMAGE = "image/*";

    public static String getExtension(String uri){
        if (uri == null){
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot>=0){
            System.out.println("uri.substring(dot)="+uri.substring(dot));
            return uri.substring(dot);

        }else
            return "";
    }
/*
    public static boolean isLocal(String url){
        if (url!= null && !url.startsWith("http://") && !url.startsWith("https://")){
            return true;
        }
        return false;
    }

    public static boolean isMediaUri(Uri uri){
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    public static Uri getUri(File file){
        if (file != null)
            return Uri.fromFile(file);
        return null;

    }


    public static String getMimeType(File file){
        String extension = getExtension(file.getName());
        System.out.println("FILEUTILS ---> extension="+extension);

        if (extension.length() >0){
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));

        }
        return "application/octet-stream";
    }

    public static String getMimeType(Context context,Uri uri){
        File file = new File(getPath(context,uri));
        return getMimeType(file);
    }
*/

    public static String getPath(final Context context, final Uri uri){
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if ( isKitKat && DocumentsContract.isDocumentUri(context,uri)) {

            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("isExternalStorageDocument --> DocID=" + docId + "  type=" + type);
                System.out.println("getExternalStorageState -->"+ Environment.getExternalStorageState());
                if ("primary".equalsIgnoreCase(type)) {
                    System.out.println(">>>"+Environment.getExternalStorageDirectory() + "/" + split[1]);
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) { //ok
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                System.out.println("isDownloadsDocument ---> ContentUri=" + contentUri);
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                System.out.println("isMediaDocument  --> DocID=" + docId + "  type=" + type);

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    System.out.println("contentUri = " + contentUri);

                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                System.out.println("IsMediaContent ---> Context=" + context + " contentUri=" + contentUri + "  selection=" + selection + "  selectionArgs=" + selectionArgs);
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        else
        if ("content".equalsIgnoreCase(uri.getScheme())){
            System.out.println("uri.getScheme()="+uri.getScheme());
            if (isGooglePhotosUri(uri)){

                System.out.println("IsGooglePhotosURi");
                return uri.getLastPathSegment();}
            System.out.println("not isGooglePhotosUri  => context="+context+"  uri="+uri);
            return getDataColumn(context,uri,null,null);
        }
        else
        if ("file".equalsIgnoreCase(uri.getScheme())){
            System.out.println("uri.getScheme()="+uri.getScheme());
            System.out.println("fileUtils class => File ----->uri-= "+uri.getPath());
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs){
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try{
            cursor = context.getContentResolver().query(uri, projection,selection,selectionArgs,null);
            System.out.println("getDataColumn()---> cursor="+cursor+" Projection="+projection+" Selection="+selection+" SelectionARGS="+selectionArgs);
            if (cursor != null && cursor.moveToFirst()){
                final int index = cursor.getColumnIndexOrThrow(column);
                System.out.println("getDataColumn() ---> Index="+index);

                return cursor.getString(index);
            }
        }finally {
            if (cursor!= null)
                cursor.close();
        }
        return null;

    }

    public static boolean isMediaDocument(Uri uri){

        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isExternalStorageDocument(Uri uri){
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

   /* public static boolean isInternalStorageDocument(Uri uri){
        System.out.println("isInternalStorage ---> uri ="+uri.getAuthority());
        return "com.android.internalstorage.documents".equals(uri.getAuthority());
    }*/

    public static boolean isDownloadsDocument(Uri uri){

        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isGooglePhotosUri(Uri uri){

        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}

