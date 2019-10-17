package com.wack.pop2.physics.util.iterators;

import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.Iterator;

public class ContactIterator implements Iterator<Contact> {

    private Contact mCurrentContact = null;

    private ContactIterator(Contact firstContact) {
        mCurrentContact = firstContact;
    }

    /**
     * Given a {@link World} returns an iterator for all the contacts in it
     */
    public static Iterator<Contact> instanceOf(World world) {
        return new ContactIterator(world.getContactList());
    }

    @Override
    public boolean hasNext() {
        return mCurrentContact != null;
    }

    @Override
    public Contact next() {
        Contact toReturn = mCurrentContact;
        mCurrentContact = mCurrentContact.getNext();
        return toReturn;
    }
}
