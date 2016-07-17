package edu.ncu.yang.engin;

import edu.ncu.yang.domain.Contacts;
import edu.ncu.yang.domain.Picture;
import edu.ncu.yang.domain.Sms;

public class BackupFactory {
	public static Backup<Sms> createMessageBackup(){
		return new SmsService();
	}
	public static Backup<Picture> createPictureBackup(){
		return new PictureService();
	}
	public static Backup<Contacts>  createContactsBackup(){
		return new ContactsService();
	}
}
