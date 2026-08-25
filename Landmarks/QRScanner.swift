//
//  QRScanner.swift
//  Landmarks
//
//  Created by Ken Samel on 4/26/23.
//  Copyright © 2023 Ken Samel. All rights reserved.
//
//  Thursday, May 11, 2023 @ 12:04:30
//

import SwiftUI
import AVFoundation

struct QRScannerView: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var audioManager: AudioManager
    @State private var scannedPayload = ""
    @State private var isSessionReady = false

    var body: some View {
        VStack(spacing: 0) {

            // Top Bar
            HStack {
                Image(systemName: "ear")
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Text("Say What?")
                    .font(.title2)
                    .bold()
                    .foregroundColor(AppTheme.blue)

                Spacer()

                Image(systemName: "gearshape")
                    .foregroundColor(AppTheme.blue)
            }
            .padding()
            .background(Color.white)

            VStack(spacing: 28) {
                Text("Scan QR Code")
                    .font(.title)
                    .bold()
                    .foregroundColor(AppTheme.text)
                    .padding(.top, 25)

                Text("Point camera at venue QR code to\nconnect automatically.")
                    .font(.body)
                    .multilineTextAlignment(.center)
                    .foregroundColor(AppTheme.text)

                ZStack {
                    RoundedRectangle(cornerRadius: 20)
                        .fill(Color.gray.opacity(0.55))
                        .frame(width: 320, height: 320)

                    QRScanner { payload in
                        scannedPayload = payload
                    }
                        .frame(width: 250, height: 250)
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(AppTheme.blue, lineWidth: 3)
                        )
                }

                Button(action: connectScannedSession) {
                    HStack {
                        Image(systemName: "qrcode.viewfinder")
                        Text("Use Scan")
                            .bold()
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(AppTheme.blue)
                    .cornerRadius(10)
                }
                .padding(.horizontal, 24)
                .disabled(scannedPayload.isEmpty)

                NavigationLink(destination: ListeningView(), isActive: $isSessionReady) {
                    EmptyView()
                }

                NavigationLink(destination: SayWhat()) {
                    HStack {
                        Image(systemName: "arrow.left")
                        Text("Back")
                            .bold()
                    }
                    .foregroundColor(AppTheme.blue)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(AppTheme.blue, lineWidth: 1.5)
                    )
                }
                .padding(.horizontal, 24)
            }

            Spacer()

            BottomNavBar(selectedTab: .scan)
        }
        .background(AppTheme.background)
        .navigationBarHidden(true)
    }

    private func connectScannedSession() {
        guard let session = appState.connect(using: scannedPayload) else { return }
        audioManager.loadStream(from: session.streamURL.absoluteString)
        isSessionReady = true
    }
}

struct QRScanner: UIViewControllerRepresentable {
    let onCodeScanned: (String) -> Void

    func makeCoordinator() -> Coordinator {
        Coordinator(onCodeScanned: onCodeScanned)
    }

    func makeUIViewController(context: Context) -> QRScannerController {
        let controller = QRScannerController()
        controller.delegate = context.coordinator
        return controller
    }

    func updateUIViewController(_ uiViewController: QRScannerController, context: Context) {
    }

    final class Coordinator: NSObject, AVCaptureMetadataOutputObjectsDelegate {
        private let onCodeScanned: (String) -> Void

        init(onCodeScanned: @escaping (String) -> Void) {
            self.onCodeScanned = onCodeScanned
        }

        func metadataOutput(
            _ output: AVCaptureMetadataOutput,
            didOutput metadataObjects: [AVMetadataObject],
            from connection: AVCaptureConnection
        ) {
            guard let code = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
                  let value = code.stringValue else { return }
            onCodeScanned(value)
        }
    }
}

class QRScannerController: UIViewController {
    var captureSession = AVCaptureSession()
    var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    var qrCodeFrameView: UIView?
    var delegate: AVCaptureMetadataOutputObjectsDelegate?

    override func viewDidLoad() {
        super.viewDidLoad()

        guard let captureDevice = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back) else {
            print("Failed to connect to the camera")
            return
        }

        let videoInput: AVCaptureDeviceInput

        do {
            videoInput = try AVCaptureDeviceInput(device: captureDevice)
        } catch {
            print(error)
            return
        }

        captureSession.addInput(videoInput)

        let captureMetadataOutput = AVCaptureMetadataOutput()
        captureSession.addOutput(captureMetadataOutput)

        captureMetadataOutput.setMetadataObjectsDelegate(delegate, queue: DispatchQueue.main)
        captureMetadataOutput.metadataObjectTypes = [.qr]

        videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        videoPreviewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill
        videoPreviewLayer?.frame = view.layer.bounds

        if let videoPreviewLayer = videoPreviewLayer {
            view.layer.addSublayer(videoPreviewLayer)
        }

        DispatchQueue.global(qos: .background).async {
            self.captureSession.startRunning()
        }
    }
}
