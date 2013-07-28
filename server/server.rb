require 'sinatra'
require 'pp'

class WifiLocater
  attr_accessor :wifi_signals

  def initialize
    @routers = %w(skynet, SKYNET, skyNet)
    @wifi_signals = []
  end

end

class WifiSignal
  # Distance is in feet
  attr_accessor :timestamp, :wifi_data

  def initialize(timestamp, wifi_data)
    @timestamp = timestamp
    @wifi_data = wifi_data
  end
end

class WifiDatum
  attr_accessor :strength, :rssi

  def initialize(strength, rssi)
    @strength = strength
    @rssi = rssi
  end

  def distance
    distance = -34.522429396286 - 0.90716469409842 * @strength
  end
end

wifi_locater = WifiLocater.new

post '/android' do
  timestamp = params['time']
  data = params['wifiData']
  wifi_data = []
  data.each do |datum|
    wifi_data << WifiDatum.new(datum['strength'], datum['ssid'])
  end

  wifi_signal = WifiSignal.new(timestamp, wifi_data)
  wifi_locater.wifi_signals << wifi_signal

  pp wifi_locater.wifi_data
  "hi"
end

get '/' do
  "blah"
end
