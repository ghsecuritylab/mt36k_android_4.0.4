# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Emit commands needed for Motorola devices during OTA installation
(installing the MBM, CDT, LBL and BP images)."""
import os
import common

def FullOTA_InstallEnd(info):
    out_put_path = os.getenv('ANDROID_PREBUILT_DIR')

    WriteToOTAPackage(info,"/aq",out_put_path,"AQ.bin")  
    WriteToOTAPackage(info,"/pq",out_put_path,"pq.bin")  
    WriteToOTAPackage(info,"/uboot_env",out_put_path,"uenv.bin")  
    WriteToOTAPackage(info,"/uboot",out_put_path,"uboot.img")    
    
def IncrementalOTA_InstallEnd(info):

    WriteToIncrementalPackage(info,"/aq","AQ.bin")  
    WriteToIncrementalPackage(info,"/pq","pq.bin")  
    WriteToIncrementalPackage(info,"/uboot_env","uenv.bin")  

def WriteToOTAPackage(info, dev_name, bin_path, bin_name):
  try:
    common.ZipWriteStr(info.output_zip, bin_name,
                       open(bin_path + '/' + bin_name).read())

  except KeyError:
    print ("warning: no "+ bin_name +" in input target_files; ")

  try:
    info.script.WriteRawImage(dev_name, bin_name)
  except KeyError:
    print ("warning: "+ bin_name +" write script failed;")

def WriteToIncrementalPackage(info, dev_name, bin_name):
  try:
    file_name =  bin_name;
    target = info.target_zip.read(file_name);
    try:
      source = info.source_zip.read(file_name);
      if source == target:
        print(dev_name + " image unchanged; skipping")
      else:
        print(dev_name + " image changed; including")
        common.ZipWriteStr(info.output_zip, bin_name,target)
    except KeyError:
      print("warning: no "+ bin_name +" in source_files; just use target")
      WriteToOTAPackage(info, dev_name,out_put_path, bin_name)
  except KeyError:
    print("warning: no "+ bin_name +" in target_files; not flashing")
