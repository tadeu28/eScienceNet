/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.esciencenet.searchmanager;

/**
 *
 * @author Tadeu Classe
 */
public enum SearchManagerEnum {

    FILE_SEARCH{
        @Override
        public String toString() {
            return "[FILE_SEARCH]";
        }
    },
    SERVICE_SEARCH{
        @Override
        public String toString() {
            return "[SERVICE_SEARCH]";
        }
    },
    RESOURCE_SEARCH{
        @Override
        public String toString() {
            return "[RESOURCE_SEARCH]";
        }
    },
    FILE_ANSWERS{
        @Override
        public String toString() {
            return "[FILE_ANSWERS]";
        }
    },
    SERVICE_ANSWERS{
        @Override
        public String toString() {
            return "[SERVICE_ANSWERS]";
        }
    },
    CONN_SEARCH{
        @Override
        public String toString() {
            return "[CONN_SEARCH]";
        }
    },
    CONN_RES{
        @Override
        public String toString() {
            return "[CONN_RES]";
        }
    };    
    
}
