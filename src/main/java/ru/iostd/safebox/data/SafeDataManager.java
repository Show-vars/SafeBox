package ru.iostd.safebox.data;

import ru.iostd.safebox.exceptions.InvalidFileFormatException;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.LocalDateTime;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import ru.iostd.safebox.data.SafeData;
import ru.iostd.safebox.data.SafeRecord;
import ru.iostd.safebox.exceptions.BadMasterKeyException;

public class SafeDataManager {

    private SafeData safeData = new SafeData();
    private File dbFile;
    private File keyFile;

    public void load() throws FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidFileFormatException, InvalidAlgorithmParameterException, BadMasterKeyException {
        DataInputStream dbr = new DataInputStream(new FileInputStream(dbFile));
        BufferedReader kr = new BufferedReader(new InputStreamReader(new FileInputStream(keyFile)));

        char[] ctmp = new char[4];
        kr.read(ctmp);

        if (!String.valueOf(ctmp).equals("SBMK")) {
            throw new InvalidFileFormatException("Invalid file format or master key is corupted");
        }

        byte[] b = new byte[4];
        dbr.read(b);

        if (!(new String(b, Charset.forName("UTF-8"))).equals("SBDB")) {
            throw new InvalidFileFormatException("Invalid file format or database is corupted");
        }

        byte[] keyBytes = Base64.decodeBase64(kr.readLine());

        int ivLength = dbr.readInt();
        byte[] iv = new byte[ivLength];
        dbr.read(iv);

        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        Reader r = new InputStreamReader(new CipherInputStream(dbr, cipher));

        char[] c = new char[5];
        r.read(c);
        if(!String.valueOf(c).equals("JSON:")) {
            throw new BadMasterKeyException("Invalid master key");
        }
        Gson gson = new Gson();
        safeData = gson.fromJson(r, SafeData.class);

        r.close();
        kr.close();

    }

    public void save() throws FileNotFoundException, IOException, InvalidFileFormatException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        DataOutputStream dout = new DataOutputStream(new FileOutputStream(dbFile));
        BufferedReader kr = new BufferedReader(new InputStreamReader(new FileInputStream(keyFile)));

        char[] ctmp = new char[5];
        kr.read(ctmp);

        if (!String.valueOf(ctmp).equals("SBMK:")) {
            throw new InvalidFileFormatException("Invalid file format or master key is corupted");
        }

        byte[] keyBytes = Base64.decodeBase64(kr.readLine());

        dout.write("SBDB".getBytes(Charset.forName("UTF-8")));
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] iv = cipher.getIV();
        dout.writeInt(iv.length);
        dout.write(iv);
        OutputStreamWriter r = new OutputStreamWriter(new CipherOutputStream(dout, cipher));

        r.write("JSON:", 0, 5);
        Gson gson = new Gson();

        gson.toJson(safeData, r);

        r.close();
        kr.close();
    }

    public void newMasterKey() throws FileNotFoundException, IOException {
        if (keyFile.exists()) {
            keyFile.delete();
        }
        keyFile.createNewFile();

        DataOutputStream dout = new DataOutputStream(new FileOutputStream(keyFile));

        dout.write("SBMK:".getBytes(Charset.forName("UTF-8")));

        Random r = new Random();
        byte[] bytes = new byte[16];
        r.nextBytes(bytes);

        dout.write(Base64.encodeBase64String(bytes).getBytes("UTF-8"));

        dout.close();
    }

    public void newDatabase() throws IOException, FileNotFoundException, InvalidFileFormatException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        if (dbFile.exists()) {
            dbFile.delete();
        }
        dbFile.createNewFile();

        safeData = new SafeData();
        safeData.getRecords().add(new SafeRecord("Google Account", "show.vars@gmail.com", "sdfsadfsdf", "http://google.com", "", LocalDateTime.now()));
        save();
    }

    public void setFiles(File dbFile, File keyFile) {
        this.dbFile = dbFile;
        this.keyFile = keyFile;
    }

    public SafeData getData() {
        return safeData;
    }
    private static SafeDataManager instance;

    public static SafeDataManager getInstance() {
        return instance == null ? instance = new SafeDataManager() : instance;
    }
}
