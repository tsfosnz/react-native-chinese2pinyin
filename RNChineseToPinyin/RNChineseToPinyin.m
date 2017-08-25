//
//  RNChineseToPinyin.h
//  RNChineseToPinyin
//
//  Copyright © 2017 Tom Silver
//  @email workingasprogrammer@gmail.com.
//  @License MIT
//

#import "RNChineseToPinyin.h"

@implementation RNChineseToPinyin

RCT_EXPORT_MODULE();

// This would name the module AwesomeCalendarManager instead
// RCT_EXPORT_MODULE(AwesomeCalendarManager);

- (NSDictionary *)constantsToExport
{
    return @{ @"firstDayOfTheWeek": @"Monday" };
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(getPinyinSync:(NSString *)characters)
{
    // RCTLogInfo(@"Pretending to create an event %@", characters);
    
    
    // RCTLogInfo(@"Pinyin %@", [self toFullPinyin: characters]);
    // RCTLogInfo(@"Pinyin %@", [self toPinYinfirstOnly: characters]);
    
    NSString *full = nil;
    NSString *abbr = nil;
    
    if (characters == NULL) {
        return @{@"full": @"", @"abbr": @""};
    }
    
    full = [self toFullPinyin: [self filterString: characters]];
    abbr = [self toAbbrPinyin: [self filterString: characters]];
    
    if (full == nil || abbr == nil) {
        NSError *error = [NSError errorWithDomain:@"world" code:200 userInfo: nil];
        
        return @{@"full": @"", @"abbr": @""};
    }
    
    
    NSDictionary *pinyin = @{
                             @"full": full,
                             @"abbr": abbr
                             };
    
    return [NSDictionary dictionaryWithDictionary:pinyin];
    
}


RCT_EXPORT_METHOD(getPinyin:(NSString *)characters resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    
    
    // RCTLogInfo(@"Pinyin %@", [self toFullPinyin: characters]);
    // RCTLogInfo(@"Pinyin %@", [self toPinYinfirstOnly: characters]);
    NSString *full = nil;
    NSString *abbr = nil;
    
    if (characters == NULL) {
        resolve(@{@"full": @"", @"abbr": @""});
    }
    
    full = [self toFullPinyin: [self filterString: characters]];
    abbr = [self toAbbrPinyin: [self filterString: characters]];
    
    if (full == nil || abbr == nil) {
        NSError *error = [NSError errorWithDomain:@"world" code:200 userInfo: nil];
        
        reject(@"", @"", error);
        return;
    }
    
    NSDictionary *pinyin = @{
                             @"full": full,
                             @"abbr": abbr
                             };
    
    
    resolve([NSDictionary dictionaryWithDictionary:pinyin]);
    
}

- (NSString *)filterString: (NSString *)aString
{
    NSMutableString *str = [NSMutableString stringWithCapacity:aString.length];
    
    for (int i = 0; i < [aString length]; ++i) {
        unichar c = [aString characterAtIndex:i];
        NSString *r = [NSString stringWithFormat:@"%C", c];
        
        // that's CJK chinese
        if (c >= 0x4e00 && c <= 0x9fea) {
            [str appendString:r];
        } else {
            NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"^[a-zA-Z0-9]$" options:0 error:NULL];
            NSRange match = [regex rangeOfFirstMatchInString:r options:0 range:NSMakeRange(0, [r length])];
            
            if (match.length > 0) {
                [str appendString:r];
            }
        }
    }
    
    return str;
}

- (NSString *)toFullPinyin:(NSString *)aString
{
    NSMutableString *str = [aString mutableCopy];
    BOOL ret = false;
    
    ret = CFStringTransform((__bridge CFMutableStringRef)str, NULL, kCFStringTransformMandarinLatin, NO);
    
    if (ret) {
        ret = CFStringTransform((__bridge CFMutableStringRef)str, NULL, kCFStringTransformStripCombiningMarks, NO);
    }
    // NSLog(@"%@", pinyin);
    
    if (!ret) {
        return @"";
    }
    
    return [[str uppercaseString] stringByReplacingOccurrencesOfString:@" " withString:@""];
}

- (NSString *)toAbbrPinyin: (NSString *)aString
{
    
    NSMutableString *str = [NSMutableString stringWithCapacity: [aString length]];
    
    for (int i = 0; i < [aString length]; ++i) {
        
        NSString *s = [NSString stringWithFormat:@"%C", [aString characterAtIndex:i]];
        
        
        // RCTLogInfo(@"Pretending to create an event %@", s);
        
        [str appendString: [self toPinYinfirstOnly:s]];
    }
    
    if ([str length] <= 0) {
        return @"";
    }
    
    return [[str uppercaseString] stringByReplacingOccurrencesOfString:@" " withString:@""];
}


- (NSString *)toPinYinfirstOnly:(NSString *)aString
{
    NSMutableString *str = [NSMutableString stringWithString:aString];
    BOOL ret = false;
    
    ret = CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformMandarinLatin,NO);
    if (ret) {
        CFStringTransform((CFMutableStringRef)str,NULL, kCFStringTransformStripDiacritics,NO);
    }
    
    if (!ret) {
        return @"";
    }
    NSString *pinYin = [str uppercaseString];
    return [pinYin substringToIndex:1];
}


@end
