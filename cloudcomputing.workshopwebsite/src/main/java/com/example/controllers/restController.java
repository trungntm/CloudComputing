package com.example.controllers;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.models.HomePage;
import com.example.models.News;
import com.example.service.HomepageServiceImpl;
import com.example.service.NewsServiceImpl;
import com.google.api.services.drive.Drive;
//
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.stereotype.Controller;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

// config size
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 200, // 2MB
maxFileSize = 1024 * 1024 * 100, // 100MB
maxRequestSize = 1024 * 1024 * 200) // 500MB


@RestController
public class restController {
	public static final String SAVE_DIRECTORY = "UploadFile";
	
	@Autowired
	private HomepageServiceImpl homepageService;
	@Autowired
	private NewsServiceImpl newsService;
	
	@RequestMapping(value="/LoadHomePage",method=RequestMethod.POST)
	public ResponseEntity<?> LoadHomePage(){
		List<HomePage> lstHomeNews=homepageService.findAllHomeNewses();
		
		return ResponseEntity.ok(lstHomeNews);
	}
	
	@RequestMapping(value="/SaveHomePage1",method=RequestMethod.POST)
	public ResponseEntity<?> SaveHomePage1(HttpServletRequest request){
		int id=0;
		String content=request.getParameter("content");
		try {
			id=Integer.parseInt(request.getParameter("id"));
		}catch (NumberFormatException e) {
		}
		try {
			HomePage home=homepageService.findHomeNews(id);
			home.setDate_created(new Date());
			home.setContent(content);
			homepageService.savePage(home);
			return ResponseEntity.ok(home);
		}catch (Exception e) {
			return null;
		}
	}
	
	@RequestMapping(value="/SaveHomePage2",method=RequestMethod.POST)
	public ResponseEntity<?> SaveHomePage2(HttpServletRequest request){
		int id=0;
		String content=request.getParameter("content");
		try {
			id=Integer.parseInt(request.getParameter("id"));
		}catch (NumberFormatException e) {
		}
		try {
			HomePage home=homepageService.findHomeNews(id);
			home.setDate_created(new Date());
			home.setContent(content);
			homepageService.savePage(home);
			return ResponseEntity.ok(home);
		}catch (Exception e) {
			return null;
		}
	}
	
	@RequestMapping(value="/GetAllNews",method=RequestMethod.POST)
	public ResponseEntity<?> GetAllNews(){
		List<News> lstNews=newsService.findAllNews();
		if(lstNews==null) {
			return null;
		}
		return ResponseEntity.ok(lstNews);
	}
	
	@RequestMapping(value="/SaveNewsContent",method=RequestMethod.POST)
	public ResponseEntity<?> SaveNewsContent(HttpServletRequest request){
		int id=0;
		String content=request.getParameter("content");
		try {
			id=Integer.parseInt(request.getParameter("id"));
		}catch (NumberFormatException e) {
			return null;
		}
		try {
			News news=newsService.findNews(id);
			if(news==null) return null;
			news.setDate_created(new Date());
			news.setNews_content(content);
			newsService.saveNews(news);
			return ResponseEntity.ok(news);
		}catch (Exception e) {
			return null;
		}
	}
	
	// -------> admin
	@RequestMapping(value="/create-news", method=RequestMethod.POST)
	public ResponseEntity<?> CreateNews(HttpServletRequest request) {
		try {
		String newstitle = request.getParameter("newstitle");
//		Date datecreate = new Date(request.getParameter("datecreate"));
		Date datecreate = new Date();
		String newscontent = request.getParameter("newscontent");
		String FileID = request.getParameter("FileID");
		String ImageID = request.getParameter("ImageID");
		
		try {
			News news = new News(newstitle, newscontent, ImageID, datecreate, FileID);
			newsService.saveNews(news);
			return ResponseEntity.ok(news);
		}catch(Exception ex) {
			return null;
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@PostMapping("/uploadImage")
    public @ResponseBody List<String> handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) throws Exception {

		if (file.isEmpty()) {
			System.out.println("rong");
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return null;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get("file-upload/" + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
        	System.out.println("file error");
            e.printStackTrace();
        }
        
		
        Drive service = getDriveService();
        
        File fileMetadata = new File();
        fileMetadata.setTitle(file.getOriginalFilename());
        
        
        java.io.File filePath = new java.io.File("file-upload/"+file.getOriginalFilename());
        System.out.println(file.getOriginalFilename());
        
        FileContent mediaContent = new FileContent(file.getContentType(), filePath);
        
        File f = service.files().insert(fileMetadata, mediaContent)
            .setFields("id")
            .execute();
        String link = "https://drive.google.com/uc?export=view&id=" + f.getId();
        request.setAttribute("LinkFileImage", link);
        request.setAttribute("IdImage", f.getId());
        
        //
        Permission newPermission = new Permission();
        newPermission.setValue("");
        newPermission.setType("anyone");
        newPermission.setRole("reader");
        service.permissions().insert(f.getId(), newPermission).execute();
        //
        List<String> result = new ArrayList<>();
        result.add(link);
        result.add(f.getId());
        return result;
        
    }
	
	
	/** Application name. */
    private static final String APPLICATION_NAME = "Drive API Java Quickstart";

    /** Directory to store user credentials for this application. */
    /*private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/drive-java-quickstart");*/
    
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
    		System.getProperty("user.home"), ".credentials/2/drive-java-quickstart.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/drive-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);
    
    

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws Exception 
     */
    public static Credential authorize() throws Exception {
        // Load client secrets.
        InputStream in =
        		controller.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        
        return credential;
    }

    /**
     * Build and return an authorized Drive client service.
     * @return an authorized Drive client service
     * @throws Exception 
     */
    public static Drive getDriveService() throws Exception {
        Credential credential = authorize();
        return new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    @PostMapping("/uploadFile")
    public @ResponseBody List<String> handleFileUploads(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) throws Exception {

		if (file.isEmpty()) {
            
            return null;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get("file-upload/" + file.getOriginalFilename());
            System.out.println("test"+file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
        
		
        Drive service = getDriveService();
        
        File fileMetadata = new File();
        fileMetadata.setTitle(file.getOriginalFilename());
        
        
        java.io.File filePath = new java.io.File("file-upload/"+file.getOriginalFilename());
        
        FileContent mediaContent = new FileContent(file.getContentType(), filePath);
        
        File f = service.files().insert(fileMetadata, mediaContent)
            .setFields("id")
            .execute();
        //
        Permission newPermission = new Permission();
        newPermission.setValue("");
        newPermission.setType("anyone");
        newPermission.setRole("reader");
        service.permissions().insert(f.getId(), newPermission).execute();
        //
        List<String> result = new ArrayList<>();
        result.add(file.getOriginalFilename());
        result.add(f.getId());
        return result;
        
    }
	
    
    @PostMapping("/UpLoadFile")
    public @ResponseBody List<String> HandleFileUploads(@RequestParam("file2") MultipartFile file, RedirectAttributes redirectAttributes, HttpServletRequest request) throws Exception {

		if (file.isEmpty()) {
            
            return null;
        }

        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get("file-upload/" + file.getOriginalFilename());
            System.out.println("test"+file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }
        
		
        Drive service = getDriveService();
        
        File fileMetadata = new File();
        fileMetadata.setTitle(file.getOriginalFilename());
        
        
        java.io.File filePath = new java.io.File("file-upload/"+file.getOriginalFilename());
        
        FileContent mediaContent = new FileContent(file.getContentType(), filePath);
        
        File f = service.files().insert(fileMetadata, mediaContent)
            .setFields("id")
            .execute();
        //
        Permission newPermission = new Permission();
        newPermission.setValue("");
        newPermission.setType("anyone");
        newPermission.setRole("reader");
        service.permissions().insert(f.getId(), newPermission).execute();
        //
        List<String> result = new ArrayList<>();
        result.add(file.getOriginalFilename());
        result.add(f.getId());
        return result;
        
    }
    
    
    @PostMapping("/search-news")
    public @ResponseBody List<News> SearchNews(HttpServletRequest request) {
    	List<News> result = new ArrayList<>();
    	result = newsService.searchNews(request.getParameter("SearchValue"));
    	return result;
    }
    
    @PostMapping("/find-news")
    public @ResponseBody News FindNews(HttpServletRequest request) {
    	News result = new News();
    	int id = Integer.parseInt(request.getParameter("id"));
    	
    	result = newsService.findNews(id);
    	return result;
    }
    
    @PostMapping("/update-news")
    public @ResponseBody int  UpdateNews(HttpServletRequest request) {
    	News result = new News();
    	int id = Integer.parseInt(request.getParameter("newsid"));
    	result = newsService.findNews(id);
    	if(result==null) {
    		return 500;
    	}else {
    		try {
	    		String newstitle = request.getParameter("newstitle");
//	    		System.out.println(newstitle);
	    		String newscontent = request.getParameter("newscontent");
	    		String InamgeID = request.getParameter("ImageID");
	    		String FileID = request.getParameter("FileID");
	    		result.setNews_title(newstitle);
	    		System.out.println(result.getNews_title());
	    		result.setNews_content(newscontent);
	    		result.setDate_created(new Date());
	    		result.setNews_file(FileID);
	    		result.setNews_image(InamgeID);
	    		newsService.saveNews(result);
	    		return 200;
    		}catch(Exception ex) {
    			return 500;
    		}
    		
    	}
    }
    
    @PostMapping("/delete-news")
    public @ResponseBody int  DeleteNew(HttpServletRequest request) {
    	News result = new News();
    	int id = Integer.parseInt(request.getParameter("id"));
    	result = newsService.findNews(id);
    	if(result==null) {
    		return 500;
    	}else {
    		try {
	    		newsService.deleteNews(id);
	    		return 200;
    		}catch(Exception ex) {
    			return 500;
    		}
    		
    	}
    }
    
	
}
