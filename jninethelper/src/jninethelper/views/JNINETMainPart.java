package jninethelper.views;



//import TestForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import jninethelper.Activator;
//
//import classlibnoui.ClassNoUI ;
//
//import system.drawing.Size;
//import system.drawing.SizeF;
//import system.windows.forms.Form;
//import system.windows.forms.Application;
import net.sf.jni4net.Bridge;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.jarpackager.IJarExportRunnable;
import org.eclipse.jdt.ui.jarpackager.JarPackageData;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

import eclipse_net_lib.XML2JavaCodeGenerator;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout; 

import static java.nio.file.StandardCopyOption.* ;





public class JNINETMainPart extends ViewPart {

	
	public static final String ID = "jniplugin.views.MyFormPart"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Button chkDeleteAllFirst ;
	private Button chkKeepJNINetSystemFile ;
	private Button chkAddSystemFile ;
	
	private String SourceDllPath = "" ;
	private IFile dll ;
	private IFile j4ndll ;
	private IFile j4njar ;
	private IFile j4nSysJarLib ;	
	private IFile j4nUserJarLib ;

	
	private IFile jni4net_jar ;	
	private IFile jni4net_dll ;	
	private IFile jni4net_x64_dll ;	
	
    private File dllStream ;
    private File dllJ4NStream ;
    private File j4njarSteam ;

	private File jni4net_jarStream ;	
	private File jni4net_dllStream ;	
	private File jni4net_x64_dllStream ;	
 
	Combo cboCurrentBuildConfig ;
	Combo cboTargetBuildConfig ;
	Combo cboCopyTargetFolder ;
	Combo cboExtension ;
	Combo cboMultiBuildTargetFolder ;
	Button chkUseDetectedOutputExtension  ;
	
    
	//List<TextReplacement> lst ;
	
	BuildVersions bv ;
	private Text text;
	private Text txtDllPath;
	
	IMemento memento ;
	
	
//	@Override
//	public void init(IViewSite site) throws PartInitException {
//		// TODO Auto-generated method stub
//		super.init(site);
//	}


	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		// TODO Auto-generated method stub
		super.init(site, memento);
		
	
		if ( memento == null )
			return ;
        
        this.memento = memento ;
        if ( this.memento == null)
    	    JOptionPane.showMessageDialog(null, "memento is null", "", 1 );

		//this.memento = memento ;
		
		
		
		
	}


	@Override
	public void saveState(IMemento memento) {
		
	    //JOptionPane.showMessageDialog(null, "saveState", "saveState", 1 );
		
		super.saveState(memento);
		
		if ( memento == null )
			return ;

		
	    //this.memento = memento.createChild("selection");
		memento.putString( KEY_TARGET_PATH, this.txtDllPath.getText() );
		memento.putString (KEY_COPY_TARGET_FOLDER, this.cboCopyTargetFolder.getText() );
		memento.putString(KEY_FILE_EXTENSION, this.cboExtension.getText());
		
		memento.putString(KEY_TARGET_BUILD_CONFIG, this.cboTargetBuildConfig.getText());
		memento.putString(KEY_MULTIBUILD_TARGET_FOLDER, this.cboMultiBuildTargetFolder.getText());

		
	}

	
	

	public JNINETMainPart () {
				
		
	}

	
	private void getInteropFiles () 
	{
		
		
		
		//IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
//		
//		if ( Activator.getDefault() == null)
//			return ;
		
		
		try
		{
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			
		}
		catch (Exception exp1)
		{
			return ;
			
		}
		
		
		try
		
		{
			
			IEditorPart  editorPart =	Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			//Activator.getDefault().getWorkbenchWindow().getActivePage().getActiveEditor();
		
			if(editorPart  == null)
				return ;
		
			SourceDllPath = txtDllPath.getText() ;
			
			File fin = new File ( SourceDllPath ) ;
			
		    String sourceRoot =  FilenameUtils.getFullPathNoEndSeparator(SourceDllPath) ; // "S:\\1\\work\\work" ;
		    String dllName = FilenameUtils.getBaseName(SourceDllPath) ; //.getBaseName(str) + "." +  FilenameUtils.getExtension(str) ;// "ClassLibrary2" ;
		    
		
		    FilenameUtils f = new FilenameUtils () ;
		    
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    IFile file =   input.getFile();
		    IProject activeProject = file.getProject();
		    String activeProjectName = activeProject.getName();
			
		
			dll = activeProject.getFile( String.format("%s.dll",  dllName) ) ;
			j4ndll = activeProject.getFile( String.format("%s.j4n.dll",  dllName) ) ;
			j4njar = activeProject.getFile( String.format("%s.j4n.jar",  dllName) ) ;
		
			jni4net_jar = activeProject.getFile( String.format("jni4net.j-0.8.8.0.jar",  dllName) ) ;
			jni4net_dll = activeProject.getFile( String.format("jni4net.n-0.8.8.0.dll",  dllName) ) ;
			jni4net_x64_dll = activeProject.getFile( String.format("jni4net.n.w64.v40-0.8.8.0.dll",  dllName) ) ;

			
		    dllStream =new File( String.format("%s\\%s.dll", sourceRoot, dllName) );
		    dllJ4NStream =new File( String.format("%s\\%s.j4n.dll", sourceRoot, dllName) );
		    j4njarSteam =new File( String.format("%s\\%s.j4n.jar", sourceRoot, dllName) );

		    jni4net_jarStream =new File( String.format("%s\\..\\jni4net.j-0.8.8.0.jar", sourceRoot, dllName) );
		    jni4net_dllStream =new File( String.format("%s\\..\\jni4net.n-0.8.8.0.dll", sourceRoot, dllName) );
		    jni4net_x64_dllStream =new File( String.format("%s\\..\\jni4net.n.w64.v40-0.8.8.0.dll", sourceRoot, dllName) );
		    
		    
            j4nSysJarLib = activeProject.getFile( String.format("Referenced Libraries\\%s.j4n.jar",  dllName) ) ;
            j4nUserJarLib = activeProject.getFile( String.format("Referenced Libraries\\%s.jar",  dllName) ) ;


                            
            
            
		}
		catch (Exception exp)
		{
			exp.printStackTrace(); 
			
		}
          	
        
		
		
	}
	
	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		toolkit.adapt(tabFolder);
		toolkit.paintBordersFor(tabFolder);
		
		TabItem tbtmJninet = new TabItem(tabFolder, SWT.NONE);
		tbtmJninet.setText("JNINET");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmJninet.setControl(composite);
		toolkit.paintBordersFor(composite);
		
		text = new Text(composite, SWT.BORDER);
		text.setBounds(110, 46, 298, 21);
		toolkit.adapt(text, true, true);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Additional files");
		label.setBounds(10, 49, 94, 15);
		toolkit.adapt(label, true, true);
		
		
		Button btnBrowseDLL = new Button(composite, SWT.NONE);
		btnBrowseDLL.addSelectionListener(btnBrowseDLLListener());
		btnBrowseDLL.setBounds(388, 13, 20, 15);
		toolkit.adapt(btnBrowseDLL, true, true);
		btnBrowseDLL.setText("...");
		
		
		
		txtDllPath = new Text(composite, SWT.BORDER);
		txtDllPath.setText("S:\\1\\work\\workdll\\ClassLibrary2.dll");
		txtDllPath.setBounds(100, 10, 282, 21);
		toolkit.adapt(txtDllPath, true, true);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setText("Source DLL");
		label_1.setBounds(10, 13, 69, 15);
		toolkit.adapt(label_1, true, true);
		
		chkAddSystemFile = new Button(composite, SWT.CHECK);
		chkAddSystemFile.setBounds(555, 48, 93, 16);
		toolkit.adapt(chkAddSystemFile, true, true);
		chkAddSystemFile.setText("Add system files");
		
		chkDeleteAllFirst = new Button(composite, SWT.CHECK);
		chkDeleteAllFirst.setBounds(461, 48, 75, 16);
		toolkit.adapt(chkDeleteAllFirst, true, true);
		chkDeleteAllFirst.setText("Delete first");
		
		Button btnUpdateDllFile = new Button(composite, SWT.NONE);
		btnUpdateDllFile.setBounds(402, 128, 115, 25);
		btnUpdateDllFile.addSelectionListener(btnUpdateDllFileListener());
		toolkit.adapt(btnUpdateDllFile, true, true);
		btnUpdateDllFile.setText("Update DLL file");
		
		Button btnRemoveDllSupportFile = new Button(composite, SWT.NONE);
		btnRemoveDllSupportFile.setBounds(228, 128, 141, 25);
		btnRemoveDllSupportFile.addSelectionListener(btnRemoveDllSupportFileListener());
		toolkit.adapt(btnRemoveDllSupportFile, true, true);
		btnRemoveDllSupportFile.setText("Remove Dll Support Files");
		
		Button btnAddDllSupportFiles = new Button(composite, SWT.NONE);
		btnAddDllSupportFiles.setBounds(44, 128, 126, 25);
		btnAddDllSupportFiles.addSelectionListener(btnAddDllSupportFilesListener());
		toolkit.adapt(btnAddDllSupportFiles, true, true);
		btnAddDllSupportFiles.setText("Add DLL Support files");
		
		chkKeepJNINetSystemFile = new Button(composite, SWT.CHECK);
		chkKeepJNINetSystemFile.setBounds(686, 48, 109, 16);
		toolkit.adapt(chkKeepJNINetSystemFile, true, true);
		chkKeepJNINetSystemFile.setText("Keep System File");
		
		TabItem tbtmMultiBuild = new TabItem(tabFolder, SWT.NONE);
		tbtmMultiBuild.setText("Multi Build");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmMultiBuild.setControl(composite_1);
		toolkit.paintBordersFor(composite_1);
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setBounds(33, 60, 121, 15);
		toolkit.adapt(lblNewLabel_2, true, true);
		lblNewLabel_2.setText("Current config");
		
		cboCurrentBuildConfig = new Combo(composite_1, SWT.NONE);
		cboCurrentBuildConfig.setBounds(33, 91, 182, 23);
		toolkit.adapt(cboCurrentBuildConfig);
		toolkit.paintBordersFor(cboCurrentBuildConfig);
		
		cboTargetBuildConfig = new Combo(composite_1, SWT.NONE);
		cboTargetBuildConfig.setBounds(292, 91, 158, 23);
		toolkit.adapt(cboTargetBuildConfig);
		toolkit.paintBordersFor(cboTargetBuildConfig);
		
		Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setBounds(292, 60, 108, 15);
		toolkit.adapt(lblNewLabel_3, true, true);
		lblNewLabel_3.setText("Target config");
		
		Button btnParseConfig = new Button(composite_1, SWT.NONE);
		btnParseConfig.setBounds(33, 21, 93, 25);
		btnParseConfig.addSelectionListener(btnParseConfigListener());
		toolkit.adapt(btnParseConfig, true, true);
		btnParseConfig.setText("Parse Config");
		
		Button btnExecuteBuildConfigChange = new Button(composite_1, SWT.NONE);
		btnExecuteBuildConfigChange.addSelectionListener(btnExecuteBuildConfigChangeListener());
		btnExecuteBuildConfigChange.setBounds(145, 21, 158, 25);
		toolkit.adapt(btnExecuteBuildConfigChange, true, true);
		btnExecuteBuildConfigChange.setText("Execute  config update");
		
		cboMultiBuildTargetFolder = new Combo(composite_1, SWT.NONE);
		cboMultiBuildTargetFolder.setItems(new String[] {"f:\\public", "\\\\192.168.1.200\\RD-Public\\JeffreyYang", "C:\\Users\\jy\\Google Drive\\DEV", "C:\\Users\\JeffreyYang\\Google Drive"});
		cboMultiBuildTargetFolder.setBounds(324, 169, 440, 23);
		toolkit.adapt(cboMultiBuildTargetFolder);
		toolkit.paintBordersFor(cboMultiBuildTargetFolder);
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setText("Target Folder");
		label_3.setBounds(211, 172, 79, 15);
		toolkit.adapt(label_3, true, true);
		
		Button btnCopyMultiBuildOutput2 = new Button(composite_1, SWT.NONE);
		btnCopyMultiBuildOutput2.addSelectionListener(btnCopyMultiBuildOutput2Listener());
		btnCopyMultiBuildOutput2.setText("Copy project output to ");
		btnCopyMultiBuildOutput2.setBounds(23, 167, 154, 25);
		toolkit.adapt(btnCopyMultiBuildOutput2, true, true);
		
		
		
		TabItem tbtmTest = new TabItem(tabFolder, SWT.NONE);
		
		
		
		tbtmTest.setText("Test");
		
		
		
		composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmTest.setControl(composite_2);
		toolkit.paintBordersFor(composite_2);
		
		Button btnTest = new Button(composite_2, SWT.NONE);
		btnTest.setBounds(10, 10, 75, 25);
		toolkit.adapt(btnTest, true, true);
		btnTest.setText("Test");
		
		Button btnTest1 = new Button(composite_2, SWT.NONE);
		btnTest1.setBounds(91, 10, 75, 25);
		btnTest1.addSelectionListener(btnTest1Listener());
		toolkit.adapt(btnTest1, true, true);
		btnTest1.setText("Test1");
		
		Button btnRestorePrefValus = new Button(composite_2, SWT.NONE);
		btnRestorePrefValus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				RestoreCurrentValue () ;
			}
		});
		btnRestorePrefValus.setBounds(172, 10, 103, 25);
		toolkit.adapt(btnRestorePrefValus, true, true);
		btnRestorePrefValus.setText("Restore values");
		
		TabItem tbtmMisc = new TabItem(tabFolder, SWT.NONE);
		tbtmMisc.setText("Misc");
		
		Composite compositeMisc = new Composite(tabFolder, SWT.NONE);
		tbtmMisc.setControl(compositeMisc);
		toolkit.paintBordersFor(compositeMisc);
		
		Button btnCopyOutput2 = new Button(compositeMisc, SWT.NONE);
		SelectionAdapter btnCopyOutput2Listener = btnCopyOutput2ListenerDelegate();
		btnCopyOutput2.addSelectionListener(btnCopyOutput2Listener);
		btnCopyOutput2.setBounds(42, 39, 154, 25);
		toolkit.adapt(btnCopyOutput2, true, true);
		btnCopyOutput2.setText("Copy project output to ");
		
		Button btnRenamePackage = new Button(compositeMisc, SWT.NONE);
		btnRenamePackage.setBounds(42, 172, 125, 25);
		toolkit.adapt(btnRenamePackage, true, true);
		btnRenamePackage.setText("Rename package");
		
		Label lblTargetFolder = new Label(compositeMisc, SWT.NONE);
		lblTargetFolder.setBounds(230, 44, 79, 15);
		toolkit.adapt(lblTargetFolder, true, true);
		lblTargetFolder.setText("Target Folder");
		
		cboCopyTargetFolder = new Combo(compositeMisc, SWT.NONE);
		cboCopyTargetFolder.setItems(new String[] {"f:\\public", "\\\\192.168.1.200\\RD-Public\\JeffreyYang", "C:\\Users\\jy\\Google Drive\\DEV", "C:\\Users\\JeffreyYang\\Google Drive"});
		cboCopyTargetFolder.setBounds(343, 41, 440, 23);
		toolkit.adapt(cboCopyTargetFolder);
		toolkit.paintBordersFor(cboCopyTargetFolder);
		
		Label lblExtension = new Label(compositeMisc, SWT.NONE);
		lblExtension.setBounds(230, 75, 55, 15);
		toolkit.adapt(lblExtension, true, true);
		lblExtension.setText("Extension");
		
		cboExtension = new Combo(compositeMisc, SWT.NONE);
		cboExtension.setItems(new String[] {"jar", "apk", "exe"});
		cboExtension.setBounds(343, 70, 91, 23);
		toolkit.adapt(cboExtension);
		toolkit.paintBordersFor(cboExtension);
		
		chkUseDetectedOutputExtension = new Button(compositeMisc, SWT.CHECK);
		chkUseDetectedOutputExtension.setSelection(true);
		chkUseDetectedOutputExtension.setBounds(488, 75, 196, 16);
		toolkit.adapt(chkUseDetectedOutputExtension, true, true);
		chkUseDetectedOutputExtension.setText("Use detected output extension");

		createActions();
		initializeToolBar();
		initializeMenu();
		
		
		if ( memento.getString(KEY_TARGET_PATH)  != null ) this.txtDllPath.setText( memento.getString(KEY_TARGET_PATH) );
		if ( memento.getString(KEY_COPY_TARGET_FOLDER)  != null ) this.cboCopyTargetFolder.setText( memento.getString(KEY_COPY_TARGET_FOLDER) );
		if ( memento.getString(KEY_FILE_EXTENSION)  != null ) this.cboExtension.setText( memento.getString(KEY_FILE_EXTENSION) );
		  
		if ( memento.getString(KEY_TARGET_BUILD_CONFIG)  != null ) this.cboTargetBuildConfig.setText( memento.getString(KEY_TARGET_BUILD_CONFIG ) );
		if ( memento.getString(KEY_MULTIBUILD_TARGET_FOLDER)  != null ) this.cboMultiBuildTargetFolder.setText( memento.getString(KEY_MULTIBUILD_TARGET_FOLDER ) );

		
//		RestoreCurrentValue () ;
	}


	private SelectionAdapter btnRemoveDllSupportFileListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
		        try{
		        	
					
		        	getInteropFiles () ;
		            dll.delete(true, null );
		            j4ndll.delete(true, null );
		            j4njar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_jar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_dll.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_x64_dll.delete(true, null );

		            
		             
		    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
			    SaveCurrentValue () ;
				
				
			}
		};
	}


	private SelectionAdapter btnBrowseDLLListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			    dialog
			        .setFilterNames(new String[] { "NET Assembly", "All Files (*.*)" });
			    dialog.setFilterExtensions(new String[] { "*.dll", "*.*" }); 
			    String path = txtDllPath.getText() ;
			    path = FilenameUtils.getFullPathNoEndSeparator(  path ) ;
			    dialog.setFilterPath(path); // Windows path
			    //dialog.setFileName("fred.bat");
			    String str1 =  dialog.open();		
			    if ( str1.length() > 0)
			    	txtDllPath.setText(  str1  );
			    SaveCurrentValue () ;
			    
			}
		};
	}


	private SelectionAdapter btnUpdateDllFileListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			
				
		        try{
		        	
					
		        	getInteropFiles () ;
		        	
		            dll.delete(true, null );

		            dll.create(new FileInputStream(dllStream), true, null);
			             
		             
		    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
			    SaveCurrentValue () ;
			
			
			
			}
		};
	}


	private SelectionAdapter btnExecuteBuildConfigChangeListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				try
				{
					Build fromBuild = null, toBuild = null  ;
					
					for ( Build b : bv.lstBuild)
						if ( b.Name.equals(cboCurrentBuildConfig.getText() ) )
							fromBuild = b;
						else if  ( b.Name.equals( cboTargetBuildConfig.getText() ) )
							toBuild = b;
							
							
					
					
					processContainer (getActiveProject (),  fromBuild, toBuild ) ;
					
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    SaveCurrentValue () ;
				
			}
		};
	}


	private SelectionAdapter btnAddDllSupportFilesListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
					
				
		        try{
		        	
					
		        	getInteropFiles () ;
		        	
		            dll.delete(true, null );
		            j4ndll.delete(true, null );
		            j4njar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_jar.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_dll.delete(true, null );
		            if ( !chkKeepJNINetSystemFile.getSelection() ) jni4net_x64_dll.delete(true, null );

		            
		            dll.create(new FileInputStream(dllStream), true, null);
		            j4ndll.create(new FileInputStream(dllJ4NStream), true, null);
		            j4njar.create(new FileInputStream(j4njarSteam), true, null);

		            if ( chkAddSystemFile.getSelection())
		            {
		            	
			            jni4net_jar.create(new FileInputStream(jni4net_jarStream), true, null);
			            jni4net_dll.create(new FileInputStream(jni4net_dllStream), true, null);
			            jni4net_x64_dll.create(new FileInputStream(jni4net_x64_dllStream), true, null);

			            j4nSysJarLib.create(new FileInputStream(jni4net_jarStream), true, null);

		            }
		            
		            
			            
		            j4nUserJarLib.create(new FileInputStream(j4njarSteam), true, null);
		             
		             
		            	    		
					
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
				
			    SaveCurrentValue () ;
				
				
				
				
			}
		};
	}


	private SelectionAdapter btnParseConfigListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
	
					
//			      	File file = new File( getActiveProject ().getFile("BuildParam.xml").getRawLocation ().toOSString()  );
//			        JAXBContext jaxbContext = JAXBContext.newInstance(BuildVersions.class);
//			        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//			        jaxbUnmarshaller.unmarshal(file); 
//			        bv = (BuildVersions) jaxbUnmarshaller.unmarshal(file);
//					
//					
//					for ( Build b : bv.getBuilds() )
//					{
//						cboCurrentBuildConfig.add (b.getName() ) ;
//						cboTargetBuildConfig.add (b.getName() ) ;
//						
//					}
//
					
			        
	
					IFile ifile = getActiveProject ().getFile("BuildParam.xml") ;
					if ( !ifile.exists())
					{
						
			            MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Config file not found!!!", "BuildParam.xml does not exist, pleae create one in the project root"  );
			            return ;
			            
					}

					bv = ObjectFromXML.objectFromJABX( ifile.getRawLocation ().toOSString(), BuildVersions.class ) ;

					for ( Build b : bv.lstBuild )
					{
						cboCurrentBuildConfig.add (b.Name) ;
						cboTargetBuildConfig.add (b.Name) ;
						
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    SaveCurrentValue () ;

			
			}
		};
	}


	private SelectionAdapter btnCopyMultiBuildOutput2Listener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try
				{
					
				    String TargetFolder = cboMultiBuildTargetFolder.getText() ; 
				    
					
					IProject project = getActiveProject () ;
	
					Build fromBuild = null, toBuild = null  ;
					
					for ( Build b : bv.lstBuild)
						if ( b.Name.equals(cboCurrentBuildConfig.getText() ) )
							fromBuild = b;
						else if  ( b.Name.equals( cboTargetBuildConfig.getText() ) )
							toBuild = b;
					
					
					
					IFile f = project.getFile("/bin/" + project.getName() + "." +  toBuild.BuildOutput.substring(toBuild.BuildOutput.lastIndexOf('.') ) ) ;
				    String fileName =  f.getRawLocation().toOSString() ; 
	
					    
			        File source = new File( fileName );

								
			        
			        
			        File dest = new File( String.format("%s\\%s", TargetFolder, toBuild.BuildOutput ));
			 
			        Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);
				    JOptionPane.showMessageDialog(null, "done", "Success", 1 );
		        
		 
		        }catch(IOException exp){
		            exp.printStackTrace();
		        }					
			    SaveCurrentValue () ;
			
				
			}
		};
	}


	private SelectionAdapter btnCopyOutput2ListenerDelegate() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try
				{
					
					SaveCurrentValue () ;

				    String extension = cboExtension.getText() ;
				    String TargetFolder = cboCopyTargetFolder.getText() ; 


				    IProject project = getActiveProject () ;
					
					IBuildConfiguration buildconfig =  project.getActiveBuildConfig() ;
					System.out.println( buildconfig.getName());
					
					IProjectNature nature =  project.getNature("org.eclipse.jdt.core.javanature" ) ;
					if ( nature != null)
					{
						//JOptionPane.showMessageDialog(null, "Is Java nature", "Confirm", 1 );
						System.out.println( nature.toString() );
						if (  chkUseDetectedOutputExtension.getSelection() )
							extension = "jar" ;

					}
					

					IProjectNature natureAndroid =  project.getNature("com.android.ide.eclipse.adt.AndroidNature" ) ;
					if ( natureAndroid != null)
					{
					    //JOptionPane.showMessageDialog(null, "Is Android nature", "Confirm", 1 );
						System.out.println( natureAndroid.toString() );
						if (  chkUseDetectedOutputExtension.getSelection() )
							extension = "apk" ;

					}

					
					IFile f = project.getFile("/bin/" + project.getName() + "." + extension ) ;
				    String fileName =  f.getRawLocation().toOSString() ; 
	
					    
			        File source = new File( fileName );
				    String str = JOptionPane.showInputDialog("Enter FileName : ",  project.getName()  ) ;
					if ( str == null)
						return ;
			        File dest = new File( String.format("%s\\%s.%s", TargetFolder, str, extension ));
			 
			        Files.copy(source.toPath(), dest.toPath(), REPLACE_EXISTING);
				    JOptionPane.showMessageDialog(null, "done", "Success", 1 );
		        
		 
		        }catch(Exception exp){
		            exp.printStackTrace();
		        }					
			    SaveCurrentValue () ;
				
				
			}
		};
	}


	private SelectionAdapter btnTest1Listener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				
				
//				IFile[] files2 = ResourcesPlugin.getWorkspace().getRoot().getFilters().findFilesForLocationURI(new URI("file:/"+workingDirectory));
//				for (IFile file : files2) {
//				   System.out.println("fullpath " +file.getFullPath());
//				}
//				

				
				try {
					
					IProject project = getActiveProject () ;
					
					IJavaProject javaProject = JavaCore.create( project );
					 
					//set the build path
//					IClasspathEntry[] buildPath = {
//							JavaCore.newSourceEntry(project.getFullPath().append("src")),
//									JavaRuntime.getDefaultJREContainerEntry() };
//					 
//					javaProject.setRawClasspath(buildPath, project.getFullPath().append(
//									"bin"), null);
					 
					//create folder by using resources package
					IFolder folder = project.getFolder("src");
						 
					//Add folder to Java element
					IPackageFragmentRoot srcFolder = javaProject.getPackageFragmentRoot(folder);
					
					String str = srcFolder.getPath().toString() ;
					str = str.substring(1, str.length() - 4 ) ;
					
					System.out.println(str);
					
					
					
					

					try
					
					{
						 
						//JOptionPane.showMessageDialog(null, System.getProperty("user.dir"), "", 1 );
						 
						Bridge.init( new java.io.File("s:\\Eclipse") ) ;
					 	//Bridge.init();
				        //Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\JavaDev\\jninethelper\\jni4net.n-0.8.8.0.dll"));
//				        Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("S:\\AndroidGit\\jninethelper\\eclipse_net_lib.j4n.dll"));
				        Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("s:\\Eclipse\\eclipse_net_lib.j4n.dll"));
						String str2 = XML2JavaCodeGenerator.GenerateClassDefinition(getActiveFile ().getRawLocation().toOSString() , "c:\\temp" ) ;
						String str1 = XML2JavaCodeGenerator.GenerateMarshalCode( getActiveFile ().getRawLocation().toOSString() , "c:\\temp" ) ;
						
			            //MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Title", str2 );
						String strs[] = str2.split(";") ;
						for ( String strPath : strs )
						{
				            String fileName = Paths.get(strPath).getFileName().toString() ;
				            //MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Title", fileName );
					            
							IFile f = project.getFile(  "src/" + fileName ) ;
				            f.delete(true, null );
				            Thread.sleep(100);
				            f.create(new FileInputStream(strPath), true, null);
							
							
							
						}
						IFile f1 = project.getFile( String.format("src/ObjectFromXML.java" ) ) ;
			            f1.delete(true, null );
			            Thread.sleep(100);
			            f1.create(new FileInputStream(str1), true, null);
						
			            
			            

				        
						
					}
					catch (Exception exp)
					{
						MessageDialog.openInformation(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Error",
								exp.getMessage() );
						
					}					
					
					
					
					
					 
					//create package fragment
//					IPackageFragment fragment = srcFolder.createPackageFragment(
//							"com.programcreek", true, null);
//					 
//					//init code string and create compilation unit
//					String str = "package com.programcreek;" + "\n"
//						+ "public class Test  {" + "\n" + " private String name;"
//						+ "\n" + "}";
//					 
//							ICompilationUnit cu = fragment.createCompilationUnit("Test.java", str,
//									false, null);					
//					
//	
			        //System.out.println(employee.toString() ) ;
					
					//replaceText ( str, "org.micareo" , null ) ;x
					
					
					
					//replaceText (getActiveProject (), str, txtAdditionalFiles.getText() , null  ) ;
					
					//processContainer (getActiveProject (), lst , cboCurrentBuildConfig.getText()  , cboTargetBuildConfig.getText()  ) ;
					
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
			}
		};
	}

	
	
//	 void createJar(Shell parentShell) {
//	 void createJar(Shell parentShell), IType mainType, IFile[] filestoExport) {
	 void createJar(Shell parentShell, IFile[] filestoExport) {

		    JarPackageData description= new JarPackageData();
	        IPath location= (IPath) new org.eclipse.core.runtime.Path ("d:\\myjar.jar");
	        description.setJarLocation(location);
	        description.setSaveManifest(true);
	        //description.setManifestMainClass(mainType);
	        description.setElements(filestoExport);
	        IJarExportRunnable runnable= description.createJarExportRunnable(parentShell);
	        try {
	            new ProgressMonitorDialog(parentShell).run(true,true, runnable);
	        } catch (InvocationTargetException e) {
	            // An error has occurred while executing the operation
	        } catch (InterruptedException e) {
	            // operation has been canceled.
	        }
	    }
	 
	
	IProject getActiveProject ()
	{
		
			
		IEditorPart  editorPart =
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				//Activator.getDefault().getWorkbenchWindow().getActivePage().getActiveEditor();

		if(editorPart  != null)
		{
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    IFile file = input.getFile();
		    IProject activeProject = file.getProject();
		    return activeProject ;
		    
		}
		else
			
			return null ;
		
		
	}
	

	
	IFile getActiveFile ()
	{
		
			
		IEditorPart  editorPart =
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				//Activator.getDefault().getWorkbenchWindow().getActivePage().getActiveEditor();

		if(editorPart  != null)
		{
		    IFileEditorInput input = (IFileEditorInput)editorPart.getEditorInput() ;
		    return input.getFile();
		    
		}
		else
			
			return null ;
		
		
	}
	
	String getActiveFilePath (IFile ifile)
	{
		IFile f = getActiveFile () ;
		return f.getFullPath().toOSString() ;
		
		
	}
	
	
	
	void replaceText (IContainer container, String fromText, String toText, Object option ) throws CoreException
	{
	   IResource [] members = container.members();
	   for (IResource member : members)
	    {
	       if (member instanceof IContainer)
	    	   replaceText((IContainer)member, fromText, toText, option);
	       else if (member instanceof IFile)
	       {
	    	   IFile f = (IFile)member ;
	    	   String fullPath = f.getRawLocation ().toOSString() ;
	    	   System.out.println( fullPath  );
    		   replaceText ( fullPath, fromText, toText, true ) ;

    		   
	    	   
	    	   
	       }
	    }
	} 	
	
	
	
	void processContainer(IContainer container,  Build fromBuild, Build toBuild ) 
	{
		try
		{
			
			   IResource [] members = container.members();
			   for (IResource member : members)
			    {
			       if (member instanceof IContainer)
			         processContainer((IContainer)member, fromBuild, toBuild );
			       else if (member instanceof IFile)
			       {
			    	   IFile f = (IFile)member ;
			    	   String fullPath = f.getRawLocation ().toOSString() ;
			    	   System.out.println( fullPath  + " Charset = " + f.getCharset() );

//			    	   if ( f.getName().contains( "a1.java"  ))
//			    		   createJar (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new IFile [] { f }  ) ;
			    	   
			    	   
			    	   if ( f.getCharset().startsWith("UTF") || f.getFullPath().getFileExtension() == "java" ) // 
			    	   {
			    		   
			    		   
				    	   String fromText = null ;
				    	   String toText = null ;
				    	   
				    	   for ( TextReplacement  tp : fromBuild.lstTextReplacement  )
				    	   {
				    		   
					    	   if ( tp.FileName.endsWith(f.getName() )  )
					    	   {
					    		   fromText = tp.toText ;
					    		   toText =  getFromText ( toBuild, tp.ReplacementID) ;
					    		   replaceText ( fullPath, fromText, toText, false ) ;
					    		   
					    	   }
				    		   
				    	   }
			    		   
			    	   }
			    	   
			    	   
			    	   
			       }
			    }
			
			
		}
		
		catch (Exception exp)
		{
			exp.printStackTrace(); 
			
		}
	} 
	
	
	
	private String getFromText (  Build build, String ReplacementID )
	{
		
 	   for ( TextReplacement  tp : build.lstTextReplacement  )
	   {
 		   if ( tp.ReplacementID.equals(ReplacementID)  )
 			   return tp.toText  ;
 		   
	   }
 	   
 	   return null ;
 	   
		
		
	}
	
	public void replaceText (String fileIn, String fromText, String toText, boolean bWholeWord )
	{
		
		Path path = Paths.get(fileIn);
		Charset charset = StandardCharsets.UTF_8;

		String content;
		try {
			
			content = new String(Files.readAllBytes(path), charset);
			String NewContent ;
			
			if ( bWholeWord )
				NewContent = content.replaceAll("\\b" + fromText + "\\b", toText);
			else
				NewContent = content.replaceAll(fromText, toText);
				
				
			if ( !NewContent.equals(content))
				Files.write(path, NewContent.getBytes(charset));	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
	
	
	public static String KEY_TARGET_PATH = "TargetPath" ;
	public static String KEY_COPY_TARGET_FOLDER = "CopyTargetFolder" ;
	public static String KEY_FILE_EXTENSION = "FileExtension" ;
	public static String KEY_TARGET_BUILD_CONFIG = "TargetBuiildConfig" ;
	public static String KEY_MULTIBUILD_TARGET_FOLDER = "MultiBuildTargetFolder" ;
	private Composite composite_2;	
	
	private void SaveCurrentValue ()
	{
		
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

		preferences.put(KEY_TARGET_PATH, this.txtDllPath.getText() );
		preferences.put(KEY_COPY_TARGET_FOLDER, this.cboCopyTargetFolder.getText() );
		preferences.put(KEY_FILE_EXTENSION, this.cboExtension.getText());
		
		preferences.put(KEY_TARGET_BUILD_CONFIG, this.cboTargetBuildConfig.getText());
		preferences.put(KEY_MULTIBUILD_TARGET_FOLDER, this.cboMultiBuildTargetFolder.getText());
		  
		
		
	}
	

	private void RestoreCurrentValue ()
	{
		
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		this.txtDllPath.setText( preferences.get(KEY_TARGET_PATH, "c:\\temp") );
		this.cboCopyTargetFolder.setText( preferences.get(KEY_COPY_TARGET_FOLDER, "c:\\temp") );
		this.cboExtension.setText( preferences.get(KEY_FILE_EXTENSION, "jar") );
		  
		this.cboTargetBuildConfig.setText( preferences.get(KEY_TARGET_BUILD_CONFIG, "Not selected") );
		this.cboMultiBuildTargetFolder.setText( preferences.get(KEY_MULTIBUILD_TARGET_FOLDER, "c:\\temp") );
		
		
	}	
}
